<?php

namespace App\Services;

use App\Core\Database;
use App\Core\Request;
use App\Helpers\JWTHelper;
use DateInterval;
use DateTimeImmutable;
use PDO;

final class AuthService
{
    public function __construct(
        private readonly ?PDO $connection = null,
        private readonly ?OtpDeliveryService $otpDeliveryService = null
    ) {
    }

    public function register(array $input): array
    {
        $role = $this->normalizeRole($input['role'] ?? 'customer');
        $name = trim((string) ($input['name'] ?? ''));
        $phone = trim((string) ($input['phone'] ?? ''));
        $email = $this->normalizeNullableString($input['email'] ?? null);
        $password = (string) ($input['password'] ?? '');

        if ($name === '' || $phone === '' || $password === '') {
            throw new AuthException('Validation failed', 422, [
                'name' => $name === '' ? 'name is required' : null,
                'phone' => $phone === '' ? 'phone is required' : null,
                'password' => $password === '' ? 'password is required' : null,
            ]);
        }

        $this->validatePasswordPolicy($password);
        $pdo = $this->pdo();

        $this->assertUserDoesNotExist($phone, $email);

        $stmt = $pdo->prepare(
            'INSERT INTO users (name, email, phone, password_hash, role_id, is_active, is_verified, is_blocked, login_attempts, created_at, updated_at)
             VALUES (:name, :email, :phone, :password_hash, :role_id, 1, 1, 0, 0, :created_at, :updated_at)'
        );

        $now = $this->now();
        $stmt->execute([
            'name' => $name,
            'email' => $email,
            'phone' => $phone,
            'password_hash' => password_hash($password, PASSWORD_BCRYPT),
            'role_id' => $this->roleId($role),
            'created_at' => $now,
            'updated_at' => $now,
        ]);

        $user = $this->findUserById((int) $pdo->lastInsertId());

        return response(201, [
            'user' => $this->publicUser($user),
        ], 'User registered successfully');
    }

    public function loginWithPassword(array $input, Request $request): array
    {
        $role = $this->normalizeRole($input['role'] ?? null);
        $password = (string) ($input['password'] ?? '');
        $identifier = trim((string) ($input['identifier'] ?? $input['email'] ?? $input['phone'] ?? ''));
        $rememberMe = filter_var($input['remember_me'] ?? false, FILTER_VALIDATE_BOOL);

        if ($identifier === '' || $password === '' || $role === null) {
            throw new AuthException('Validation failed', 422, [
                'identifier' => $identifier === '' ? 'identifier is required' : null,
                'password' => $password === '' ? 'password is required' : null,
                'role' => $role === null ? 'role is required' : null,
            ]);
        }

        $user = $this->findUserByIdentifierAndRole($identifier, $role);
        $this->ensureUserCanAuthenticate($user);

        if (!$user || !password_verify($password, (string) $user['password_hash'])) {
            if ($user) {
                $this->recordFailedLogin((int) $user['id']);
            }

            throw new AuthException('Invalid credentials', 401, [
                'identifier' => 'The provided credentials are invalid.',
            ]);
        }

        $this->clearFailedLoginState((int) $user['id']);
        $this->updateLastLogin((int) $user['id']);

        return response(200, $this->issueTokens($user, $request, $rememberMe), 'Login successful');
    }

    public function sendOtp(array $input): array
    {
        $role = $this->normalizeRole($input['role'] ?? null);
        $purpose = $this->normalizePurpose($input['purpose'] ?? 'login');
        $identifier = trim((string) ($input['identifier'] ?? $input['email'] ?? $input['phone'] ?? ''));

        if ($identifier === '' || $role === null) {
            throw new AuthException('Validation failed', 422, [
                'identifier' => $identifier === '' ? 'identifier is required' : null,
                'role' => $role === null ? 'role is required' : null,
            ]);
        }

        $user = $this->findUserByIdentifierAndRole($identifier, $role);

        if ($purpose === 'password_reset') {
            if (!$user) {
                return response(200, [
                    'delivery' => null,
                ], 'If the account exists, reset instructions have been sent.');
            }
        } else {
            $this->ensureUserCanAuthenticate($user);
        }

        if (!$user) {
            throw new AuthException('Account not found', 404, [
                'identifier' => 'No matching account was found for the supplied role.',
            ]);
        }

        $this->enforceOtpRateLimit((int) $user['id'], $purpose);

        $channel = $this->selectDeliveryChannel($user, (string) ($input['channel'] ?? ''));
        $destination = (string) $user[$channel];
        $otp = $this->generateOtp();
        $expiresAt = $this->nowPlusSeconds((int) config('auth.otp.expiry', 300));

        $stmt = $this->pdo()->prepare(
            'INSERT INTO auth_otps (user_id, purpose, channel, destination, code_hash, expires_at, attempt_count, consumed_at, created_at, updated_at)
             VALUES (:user_id, :purpose, :channel, :destination, :code_hash, :expires_at, 0, NULL, :created_at, :updated_at)'
        );

        $now = $this->now();
        $stmt->execute([
            'user_id' => $user['id'],
            'purpose' => $purpose,
            'channel' => $channel,
            'destination' => $destination,
            'code_hash' => password_hash($otp, PASSWORD_BCRYPT),
            'expires_at' => $expiresAt,
            'created_at' => $now,
            'updated_at' => $now,
        ]);

        $delivery = $this->otpDelivery()->send($channel, $destination, $otp, $purpose);
        $data = [
            'delivery' => $delivery,
            'expires_in' => (int) config('auth.otp.expiry', 300),
        ];

        if ($this->shouldExposeSensitiveDebugData()) {
            $data['otp_preview'] = $otp;
        }

        return response(200, $data, 'OTP sent successfully');
    }

    public function verifyOtp(array $input, Request $request): array
    {
        $role = $this->normalizeRole($input['role'] ?? null);
        $purpose = $this->normalizePurpose($input['purpose'] ?? 'login');
        $identifier = trim((string) ($input['identifier'] ?? $input['email'] ?? $input['phone'] ?? ''));
        $otp = trim((string) ($input['otp'] ?? ''));
        $rememberMe = filter_var($input['remember_me'] ?? false, FILTER_VALIDATE_BOOL);

        if ($identifier === '' || $otp === '' || $role === null) {
            throw new AuthException('Validation failed', 422, [
                'identifier' => $identifier === '' ? 'identifier is required' : null,
                'otp' => $otp === '' ? 'otp is required' : null,
                'role' => $role === null ? 'role is required' : null,
            ]);
        }

        $user = $this->findUserByIdentifierAndRole($identifier, $role);
        $this->ensureUserCanAuthenticate($user);

        $otpRecord = $this->latestOtp((int) $user['id'], $purpose);
        $this->verifyOtpRecord($otpRecord, $otp);
        $this->consumeOtp((int) $otpRecord['id']);

        if ($purpose === 'password_reset') {
            return response(200, [
                'reset_token' => $this->issuePasswordResetGrant((int) $user['id'], 'otp'),
                'expires_in' => (int) config('auth.password_reset_grant_ttl', 900),
            ], 'OTP verified successfully');
        }

        $this->updateLastLogin((int) $user['id']);

        return response(200, $this->issueTokens($user, $request, $rememberMe), 'OTP verified successfully');
    }

    public function refreshTokens(array $input, Request $request): array
    {
        $refreshToken = trim((string) ($input['refresh_token'] ?? ''));

        if ($refreshToken === '') {
            throw new AuthException('Validation failed', 422, [
                'refresh_token' => 'refresh_token is required',
            ]);
        }

        $record = $this->findActiveRefreshToken($refreshToken);

        if (!$record) {
            throw new AuthException('Invalid refresh token', 401, [
                'refresh_token' => 'Refresh token is invalid, expired, or revoked.',
            ]);
        }

        $session = $this->findSessionById((int) $record['session_id']);

        if (!$session || $session['revoked_at'] !== null || !$this->futureTimestamp($session['expires_at'])) {
            $this->revokeRefreshTokensForSession((int) $record['session_id']);

            throw new AuthException('Session expired', 401, [
                'session' => 'The session is no longer active.',
            ]);
        }

        $user = $this->findUserById((int) $record['user_id']);
        $this->ensureUserCanAuthenticate($user);

        $this->revokeRefreshToken((int) $record['id'], 'rotated');

        return response(200, $this->issueTokens(
            $user,
            $request,
            (bool) $record['remember_me'],
            (int) $record['session_id']
        ), 'Token refreshed successfully');
    }

    public function logout(array $input, ?array $authContext = null): array
    {
        $refreshToken = trim((string) ($input['refresh_token'] ?? ''));

        if ($authContext && isset($authContext['session']['id'])) {
            $this->revokeSession((int) $authContext['session']['id']);
        } elseif ($refreshToken !== '') {
            $record = $this->findActiveRefreshToken($refreshToken);
            if ($record) {
                $this->revokeSession((int) $record['session_id']);
            }
        } else {
            throw new AuthException('Validation failed', 422, [
                'refresh_token' => 'refresh_token is required when no authenticated session is present.',
            ]);
        }

        return response(200, [
            'logged_out' => true,
        ], 'Logged out successfully');
    }

    public function logoutAllSessions(int $userId): array
    {
        $stmt = $this->pdo()->prepare(
            'UPDATE auth_sessions SET revoked_at = :revoked_at, updated_at = :updated_at WHERE user_id = :user_id AND revoked_at IS NULL'
        );
        $now = $this->now();
        $stmt->execute([
            'revoked_at' => $now,
            'updated_at' => $now,
            'user_id' => $userId,
        ]);

        $tokenStmt = $this->pdo()->prepare(
            'UPDATE auth_refresh_tokens SET revoked_at = :revoked_at, revoked_reason = :reason, updated_at = :updated_at WHERE user_id = :user_id AND revoked_at IS NULL'
        );
        $tokenStmt->execute([
            'revoked_at' => $now,
            'reason' => 'logout_all',
            'updated_at' => $now,
            'user_id' => $userId,
        ]);

        return response(200, [
            'logged_out_all' => true,
        ], 'All sessions have been logged out successfully');
    }

    public function forgotPassword(array $input): array
    {
        $identifier = trim((string) ($input['identifier'] ?? $input['email'] ?? $input['phone'] ?? ''));
        $role = $this->normalizeRole($input['role'] ?? null);

        if ($identifier === '' || $role === null) {
            throw new AuthException('Validation failed', 422, [
                'identifier' => $identifier === '' ? 'identifier is required' : null,
                'role' => $role === null ? 'role is required' : null,
            ]);
        }

        $user = $this->findUserByIdentifierAndRole($identifier, $role);

        if (!$user) {
            return response(200, [
                'delivery' => null,
            ], 'If the account exists, reset instructions have been sent.');
        }

        $otpResponse = $this->sendOtp([
            'identifier' => $identifier,
            'role' => $role,
            'purpose' => 'password_reset',
            'channel' => $input['channel'] ?? '',
        ]);

        $rawToken = bin2hex(random_bytes(32));
        $expiresAt = $this->nowPlusSeconds((int) config('auth.otp.expiry', 300));
        $stmt = $this->pdo()->prepare(
            'INSERT INTO password_reset_tokens (user_id, token_hash, source, expires_at, consumed_at, created_at, updated_at)
             VALUES (:user_id, :token_hash, :source, :expires_at, NULL, :created_at, :updated_at)'
        );
        $now = $this->now();
        $stmt->execute([
            'user_id' => $user['id'],
            'token_hash' => hash('sha256', $rawToken),
            'source' => 'direct',
            'expires_at' => $expiresAt,
            'created_at' => $now,
            'updated_at' => $now,
        ]);

        $data = $otpResponse['data'];

        if ($this->shouldExposeSensitiveDebugData()) {
            $data['reset_token_preview'] = $rawToken;
        }

        return response(200, $data, 'If the account exists, reset instructions have been sent.');
    }

    public function verifyPasswordReset(array $input): array
    {
        $identifier = trim((string) ($input['identifier'] ?? $input['email'] ?? $input['phone'] ?? ''));
        $role = $this->normalizeRole($input['role'] ?? null);

        if ($identifier === '' || $role === null) {
            throw new AuthException('Validation failed', 422, [
                'identifier' => $identifier === '' ? 'identifier is required' : null,
                'role' => $role === null ? 'role is required' : null,
            ]);
        }

        $user = $this->findUserByIdentifierAndRole($identifier, $role);

        if (!$user) {
            throw new AuthException('Account not found', 404, [
                'identifier' => 'No matching account was found.',
            ]);
        }

        $otp = trim((string) ($input['otp'] ?? ''));
        $token = trim((string) ($input['token'] ?? ''));

        if ($otp === '' && $token === '') {
            throw new AuthException('Validation failed', 422, [
                'otp' => 'otp or token is required',
            ]);
        }

        if ($otp !== '') {
            $record = $this->latestOtp((int) $user['id'], 'password_reset');
            $this->verifyOtpRecord($record, $otp);
            $this->consumeOtp((int) $record['id']);
        } else {
            $resetToken = $this->findActivePasswordResetToken($token, 'direct');
            if (!$resetToken || (int) $resetToken['user_id'] !== (int) $user['id']) {
                throw new AuthException('Invalid reset token', 401, [
                    'token' => 'The provided reset token is invalid or expired.',
                ]);
            }

            $this->consumePasswordResetToken((int) $resetToken['id']);
        }

        return response(200, [
            'reset_token' => $this->issuePasswordResetGrant((int) $user['id'], $otp !== '' ? 'otp' : 'token'),
            'expires_in' => (int) config('auth.password_reset_grant_ttl', 900),
        ], 'Password reset verification successful');
    }

    public function resetPassword(array $input): array
    {
        $resetToken = trim((string) ($input['reset_token'] ?? ''));
        $password = (string) ($input['password'] ?? '');
        $passwordConfirmation = (string) ($input['password_confirmation'] ?? '');

        if ($resetToken === '' || $password === '' || $passwordConfirmation === '') {
            throw new AuthException('Validation failed', 422, [
                'reset_token' => $resetToken === '' ? 'reset_token is required' : null,
                'password' => $password === '' ? 'password is required' : null,
                'password_confirmation' => $passwordConfirmation === '' ? 'password_confirmation is required' : null,
            ]);
        }

        if ($password !== $passwordConfirmation) {
            throw new AuthException('Validation failed', 422, [
                'password_confirmation' => 'password_confirmation must match password',
            ]);
        }

        $this->validatePasswordPolicy($password);

        $tokenRecord = $this->findActivePasswordResetToken($resetToken, 'grant');

        if (!$tokenRecord) {
            throw new AuthException('Invalid reset token', 401, [
                'reset_token' => 'The supplied reset token is invalid or expired.',
            ]);
        }

        $stmt = $this->pdo()->prepare(
            'UPDATE users SET password_hash = :password_hash, updated_at = :updated_at WHERE id = :id'
        );
        $stmt->execute([
            'password_hash' => password_hash($password, PASSWORD_BCRYPT),
            'updated_at' => $this->now(),
            'id' => $tokenRecord['user_id'],
        ]);

        $this->consumePasswordResetToken((int) $tokenRecord['id']);
        $this->logoutAllSessions((int) $tokenRecord['user_id']);

        return response(200, [
            'password_reset' => true,
        ], 'Password reset successfully');
    }

    public function changePassword(int $userId, array $input): array
    {
        $currentPassword = (string) ($input['current_password'] ?? '');
        $newPassword = (string) ($input['new_password'] ?? '');
        $newPasswordConfirmation = (string) ($input['new_password_confirmation'] ?? '');

        if ($currentPassword === '' || $newPassword === '' || $newPasswordConfirmation === '') {
            throw new AuthException('Validation failed', 422, [
                'current_password' => $currentPassword === '' ? 'current_password is required' : null,
                'new_password' => $newPassword === '' ? 'new_password is required' : null,
                'new_password_confirmation' => $newPasswordConfirmation === '' ? 'new_password_confirmation is required' : null,
            ]);
        }

        if ($newPassword !== $newPasswordConfirmation) {
            throw new AuthException('Validation failed', 422, [
                'new_password_confirmation' => 'new_password_confirmation must match new_password',
            ]);
        }

        $this->validatePasswordPolicy($newPassword);
        $user = $this->findUserById($userId);

        if (!$user || !password_verify($currentPassword, (string) $user['password_hash'])) {
            throw new AuthException('Current password is incorrect', 422, [
                'current_password' => 'The provided current password is incorrect.',
            ]);
        }

        $stmt = $this->pdo()->prepare(
            'UPDATE users SET password_hash = :password_hash, updated_at = :updated_at WHERE id = :id'
        );
        $stmt->execute([
            'password_hash' => password_hash($newPassword, PASSWORD_BCRYPT),
            'updated_at' => $this->now(),
            'id' => $userId,
        ]);

        return response(200, [
            'password_changed' => true,
        ], 'Password changed successfully');
    }

    public function listSessions(int $userId): array
    {
        $stmt = $this->pdo()->prepare(
            'SELECT public_id, device_id, device_name, device_type, ip_address, user_agent, remember_me, last_activity_at, expires_at, created_at
             FROM auth_sessions
             WHERE user_id = :user_id AND revoked_at IS NULL
             ORDER BY last_activity_at DESC, created_at DESC'
        );
        $stmt->execute(['user_id' => $userId]);

        return response(200, [
            'sessions' => $stmt->fetchAll() ?: [],
        ], 'Active sessions fetched successfully');
    }

    public function authenticateRequest(Request $request): array
    {
        $token = $request->bearerToken();

        if (!$token) {
            throw new AuthException('Missing authentication token', 401, [
                'auth' => 'Authorization bearer token is required.',
            ]);
        }

        $payload = JWTHelper::decode($token);

        if (!$payload || ($payload['typ'] ?? null) !== 'access') {
            throw new AuthException('Invalid token', 401, [
                'auth' => 'The supplied access token is invalid.',
            ]);
        }

        $session = $this->findSessionByPublicId((string) ($payload['sid'] ?? ''));

        if (!$session || $session['revoked_at'] !== null || !$this->futureTimestamp($session['expires_at'])) {
            throw new AuthException('Session expired', 401, [
                'auth' => 'The authenticated session is no longer active.',
            ]);
        }

        $user = $this->findUserById((int) $payload['user_id']);
        $this->ensureUserCanAuthenticate($user);

        $stmt = $this->pdo()->prepare(
            'UPDATE auth_sessions SET last_activity_at = :last_activity_at, updated_at = :updated_at WHERE id = :id'
        );
        $now = $this->now();
        $stmt->execute([
            'last_activity_at' => $now,
            'updated_at' => $now,
            'id' => $session['id'],
        ]);

        return [
            'user' => $user,
            'payload' => $payload,
            'session' => $session,
        ];
    }

    private function issueTokens(array $user, Request $request, bool $rememberMe, ?int $existingSessionId = null): array
    {
        $refreshTtl = $rememberMe
            ? (int) config('auth.remember_me_refresh_token_ttl', 2592000)
            : (int) config('auth.refresh_token_ttl', 604800);

        $session = $existingSessionId
            ? $this->refreshExistingSession($existingSessionId, $request, $refreshTtl)
            : $this->createSession((int) $user['id'], $request, $rememberMe, $refreshTtl);

        $accessTtl = (int) config('auth.access_token_ttl', 3600);
        $accessToken = JWTHelper::encode([
            'typ' => 'access',
            'user_id' => (int) $user['id'],
            'role' => $user['role'],
            'phone' => $user['phone'],
            'email' => $user['email'],
            'sid' => $session['public_id'],
        ], $accessTtl);

        $refreshToken = bin2hex(random_bytes(48));
        $refreshExpiresAt = $this->nowPlusSeconds($refreshTtl);
        $this->storeRefreshToken((int) $user['id'], (int) $session['id'], $refreshToken, $rememberMe, $refreshExpiresAt);

        return [
            'user' => $this->publicUser($user),
            'access_token' => $accessToken,
            'refresh_token' => $refreshToken,
            'token_type' => 'Bearer',
            'expires_in' => $accessTtl,
            'refresh_expires_in' => $refreshTtl,
            'remember_me' => $rememberMe,
            'session' => [
                'id' => $session['public_id'],
                'device_id' => $session['device_id'],
                'device_name' => $session['device_name'],
                'device_type' => $session['device_type'],
                'last_activity_at' => $session['last_activity_at'],
                'expires_at' => $session['expires_at'],
            ],
        ];
    }

    private function createSession(int $userId, Request $request, bool $rememberMe, int $refreshTtl): array
    {
        $publicId = bin2hex(random_bytes(16));
        $deviceId = trim((string) ($request->input('device_id', ''))) ?: bin2hex(random_bytes(8));
        $deviceName = $this->normalizeNullableString($request->input('device_name', 'Unknown device')) ?? 'Unknown device';
        $deviceType = $this->normalizeDeviceType((string) $request->input('device_type', 'web'));
        $now = $this->now();
        $expiresAt = $this->nowPlusSeconds($refreshTtl);

        $stmt = $this->pdo()->prepare(
            'INSERT INTO auth_sessions (user_id, public_id, device_id, device_name, device_type, ip_address, user_agent, remember_me, last_activity_at, expires_at, revoked_at, created_at, updated_at)
             VALUES (:user_id, :public_id, :device_id, :device_name, :device_type, :ip_address, :user_agent, :remember_me, :last_activity_at, :expires_at, NULL, :created_at, :updated_at)'
        );
        $stmt->execute([
            'user_id' => $userId,
            'public_id' => $publicId,
            'device_id' => $deviceId,
            'device_name' => $deviceName,
            'device_type' => $deviceType,
            'ip_address' => $request->ip(),
            'user_agent' => $request->userAgent(),
            'remember_me' => $rememberMe ? 1 : 0,
            'last_activity_at' => $now,
            'expires_at' => $expiresAt,
            'created_at' => $now,
            'updated_at' => $now,
        ]);

        return $this->findSessionById((int) $this->pdo()->lastInsertId());
    }

    private function refreshExistingSession(int $sessionId, Request $request, int $refreshTtl): array
    {
        $stmt = $this->pdo()->prepare(
            'UPDATE auth_sessions
             SET ip_address = :ip_address, user_agent = :user_agent, last_activity_at = :last_activity_at, expires_at = :expires_at, updated_at = :updated_at
             WHERE id = :id'
        );
        $now = $this->now();
        $stmt->execute([
            'ip_address' => $request->ip(),
            'user_agent' => $request->userAgent(),
            'last_activity_at' => $now,
            'expires_at' => $this->nowPlusSeconds($refreshTtl),
            'updated_at' => $now,
            'id' => $sessionId,
        ]);

        return $this->findSessionById($sessionId);
    }

    private function storeRefreshToken(int $userId, int $sessionId, string $refreshToken, bool $rememberMe, string $expiresAt): void
    {
        $stmt = $this->pdo()->prepare(
            'INSERT INTO auth_refresh_tokens (user_id, session_id, token_hash, remember_me, expires_at, revoked_at, revoked_reason, created_at, updated_at)
             VALUES (:user_id, :session_id, :token_hash, :remember_me, :expires_at, NULL, NULL, :created_at, :updated_at)'
        );
        $now = $this->now();
        $stmt->execute([
            'user_id' => $userId,
            'session_id' => $sessionId,
            'token_hash' => hash('sha256', $refreshToken),
            'remember_me' => $rememberMe ? 1 : 0,
            'expires_at' => $expiresAt,
            'created_at' => $now,
            'updated_at' => $now,
        ]);
    }

    private function issuePasswordResetGrant(int $userId, string $source): string
    {
        $grant = bin2hex(random_bytes(32));
        $stmt = $this->pdo()->prepare(
            'INSERT INTO password_reset_tokens (user_id, token_hash, source, expires_at, consumed_at, created_at, updated_at)
             VALUES (:user_id, :token_hash, :source, :expires_at, NULL, :created_at, :updated_at)'
        );
        $now = $this->now();
        $stmt->execute([
            'user_id' => $userId,
            'token_hash' => hash('sha256', $grant),
            'source' => 'grant:' . $source,
            'expires_at' => $this->nowPlusSeconds((int) config('auth.password_reset_grant_ttl', 900)),
            'created_at' => $now,
            'updated_at' => $now,
        ]);

        return $grant;
    }

    private function findUserByIdentifierAndRole(string $identifier, string $role): ?array
    {
        $field = filter_var($identifier, FILTER_VALIDATE_EMAIL) ? 'email' : 'phone';
        $stmt = $this->pdo()->prepare(
            "SELECT u.*, r.name AS role
             FROM users u
             INNER JOIN roles r ON r.id = u.role_id
             WHERE u.$field = :identifier AND r.name = :role
             LIMIT 1"
        );
        $stmt->execute([
            'identifier' => $identifier,
            'role' => $role,
        ]);

        return $stmt->fetch() ?: null;
    }

    private function findUserById(int $userId): ?array
    {
        $stmt = $this->pdo()->prepare(
            'SELECT u.*, r.name AS role
             FROM users u
             INNER JOIN roles r ON r.id = u.role_id
             WHERE u.id = :id
             LIMIT 1'
        );
        $stmt->execute(['id' => $userId]);

        return $stmt->fetch() ?: null;
    }

    private function findSessionByPublicId(string $publicId): ?array
    {
        if ($publicId === '') {
            return null;
        }

        $stmt = $this->pdo()->prepare(
            'SELECT * FROM auth_sessions WHERE public_id = :public_id LIMIT 1'
        );
        $stmt->execute(['public_id' => $publicId]);

        return $stmt->fetch() ?: null;
    }

    private function findSessionById(int $sessionId): ?array
    {
        $stmt = $this->pdo()->prepare(
            'SELECT * FROM auth_sessions WHERE id = :id LIMIT 1'
        );
        $stmt->execute(['id' => $sessionId]);

        return $stmt->fetch() ?: null;
    }

    private function latestOtp(int $userId, string $purpose): ?array
    {
        $stmt = $this->pdo()->prepare(
            'SELECT * FROM auth_otps WHERE user_id = :user_id AND purpose = :purpose ORDER BY id DESC LIMIT 1'
        );
        $stmt->execute([
            'user_id' => $userId,
            'purpose' => $purpose,
        ]);

        return $stmt->fetch() ?: null;
    }

    private function verifyOtpRecord(?array $record, string $otp): void
    {
        if (!$record || $record['consumed_at'] !== null || !$this->futureTimestamp($record['expires_at'])) {
            throw new AuthException('Invalid OTP', 401, [
                'otp' => 'The supplied OTP is invalid or expired.',
            ]);
        }

        $maxAttempts = (int) config('auth.otp.max_attempts', 5);

        if ((int) $record['attempt_count'] >= $maxAttempts) {
            throw new AuthException('OTP verification limit reached', 429, [
                'otp' => 'Too many OTP verification attempts. Please request a new code.',
            ]);
        }

        if (!password_verify($otp, (string) $record['code_hash'])) {
            $stmt = $this->pdo()->prepare(
                'UPDATE auth_otps SET attempt_count = attempt_count + 1, updated_at = :updated_at WHERE id = :id'
            );
            $stmt->execute([
                'updated_at' => $this->now(),
                'id' => $record['id'],
            ]);

            throw new AuthException('Invalid OTP', 401, [
                'otp' => 'The supplied OTP is invalid or expired.',
            ]);
        }
    }

    private function consumeOtp(int $otpId): void
    {
        $stmt = $this->pdo()->prepare(
            'UPDATE auth_otps SET consumed_at = :consumed_at, updated_at = :updated_at WHERE id = :id'
        );
        $now = $this->now();
        $stmt->execute([
            'consumed_at' => $now,
            'updated_at' => $now,
            'id' => $otpId,
        ]);
    }

    private function findActiveRefreshToken(string $refreshToken): ?array
    {
        $stmt = $this->pdo()->prepare(
            'SELECT * FROM auth_refresh_tokens
             WHERE token_hash = :token_hash AND revoked_at IS NULL AND expires_at > :now
             ORDER BY id DESC
             LIMIT 1'
        );
        $stmt->execute([
            'token_hash' => hash('sha256', $refreshToken),
            'now' => $this->now(),
        ]);

        return $stmt->fetch() ?: null;
    }

    private function revokeRefreshToken(int $tokenId, string $reason): void
    {
        $stmt = $this->pdo()->prepare(
            'UPDATE auth_refresh_tokens
             SET revoked_at = :revoked_at, revoked_reason = :reason, updated_at = :updated_at
             WHERE id = :id'
        );
        $now = $this->now();
        $stmt->execute([
            'revoked_at' => $now,
            'reason' => $reason,
            'updated_at' => $now,
            'id' => $tokenId,
        ]);
    }

    private function revokeRefreshTokensForSession(int $sessionId): void
    {
        $stmt = $this->pdo()->prepare(
            'UPDATE auth_refresh_tokens
             SET revoked_at = :revoked_at, revoked_reason = :reason, updated_at = :updated_at
             WHERE session_id = :session_id AND revoked_at IS NULL'
        );
        $now = $this->now();
        $stmt->execute([
            'revoked_at' => $now,
            'reason' => 'session_revoked',
            'updated_at' => $now,
            'session_id' => $sessionId,
        ]);
    }

    private function revokeSession(int $sessionId): void
    {
        $stmt = $this->pdo()->prepare(
            'UPDATE auth_sessions SET revoked_at = :revoked_at, updated_at = :updated_at WHERE id = :id'
        );
        $now = $this->now();
        $stmt->execute([
            'revoked_at' => $now,
            'updated_at' => $now,
            'id' => $sessionId,
        ]);

        $this->revokeRefreshTokensForSession($sessionId);
    }

    private function findActivePasswordResetToken(string $token, string $type): ?array
    {
        $sources = $type === 'grant' ? ['grant:otp', 'grant:token'] : ['direct'];
        $placeholders = implode(', ', array_fill(0, count($sources), '?'));
        $stmt = $this->pdo()->prepare(
            "SELECT * FROM password_reset_tokens
             WHERE token_hash = ? AND source IN ($placeholders) AND consumed_at IS NULL AND expires_at > ?
             ORDER BY id DESC
             LIMIT 1"
        );

        $params = array_merge([hash('sha256', $token)], $sources, [$this->now()]);
        $stmt->execute($params);

        return $stmt->fetch() ?: null;
    }

    private function consumePasswordResetToken(int $tokenId): void
    {
        $stmt = $this->pdo()->prepare(
            'UPDATE password_reset_tokens SET consumed_at = :consumed_at, updated_at = :updated_at WHERE id = :id'
        );
        $now = $this->now();
        $stmt->execute([
            'consumed_at' => $now,
            'updated_at' => $now,
            'id' => $tokenId,
        ]);
    }

    private function enforceOtpRateLimit(int $userId, string $purpose): void
    {
        $cooldown = (int) config('auth.otp.cooldown', 60);
        $latest = $this->latestOtp($userId, $purpose);

        if ($latest && strtotime((string) $latest['created_at']) > (time() - $cooldown)) {
            throw new AuthException('OTP recently sent', 429, [
                'otp' => 'Please wait before requesting another OTP.',
            ]);
        }

        $stmt = $this->pdo()->prepare(
            'SELECT COUNT(*) FROM auth_otps WHERE user_id = :user_id AND purpose = :purpose AND created_at >= :window_start'
        );
        $stmt->execute([
            'user_id' => $userId,
            'purpose' => $purpose,
            'window_start' => $this->nowMinusSeconds(3600),
        ]);

        $count = (int) $stmt->fetchColumn();

        if ($count >= (int) config('auth.otp.max_requests_per_hour', 5)) {
            throw new AuthException('OTP request limit reached', 429, [
                'otp' => 'Too many OTP requests. Please try again later.',
            ]);
        }
    }

    private function assertUserDoesNotExist(string $phone, ?string $email): void
    {
        $stmt = $this->pdo()->prepare(
            'SELECT COUNT(*) FROM users WHERE phone = :phone OR (:email IS NOT NULL AND email = :email)'
        );
        $stmt->execute([
            'phone' => $phone,
            'email' => $email,
        ]);

        if ((int) $stmt->fetchColumn() > 0) {
            throw new AuthException('Account already exists', 409, [
                'identifier' => 'A user already exists with the supplied phone or email.',
            ]);
        }
    }

    private function ensureUserCanAuthenticate(?array $user): void
    {
        if (!$user) {
            throw new AuthException('Account not found', 404, [
                'identifier' => 'No matching account was found for the supplied role.',
            ]);
        }

        if (!(bool) $user['is_active'] || (bool) $user['is_blocked']) {
            throw new AuthException('Account is unavailable', 403, [
                'account' => 'The account is inactive or blocked.',
            ]);
        }

        if ($user['locked_until'] && $this->futureTimestamp($user['locked_until'])) {
            throw new AuthException('Account temporarily locked', 423, [
                'account' => 'Too many failed login attempts. Please try again later.',
            ]);
        }
    }

    private function recordFailedLogin(int $userId): void
    {
        $user = $this->findUserById($userId);
        $attempts = ((int) ($user['login_attempts'] ?? 0)) + 1;
        $lockedUntil = null;
        $maxAttempts = (int) config('auth.login.max_attempts', 5);

        if ($attempts >= $maxAttempts) {
            $lockedUntil = $this->nowPlusSeconds((int) config('auth.login.lockout_seconds', 900));
            $attempts = 0;
        }

        $stmt = $this->pdo()->prepare(
            'UPDATE users SET login_attempts = :login_attempts, locked_until = :locked_until, updated_at = :updated_at WHERE id = :id'
        );
        $stmt->execute([
            'login_attempts' => $attempts,
            'locked_until' => $lockedUntil,
            'updated_at' => $this->now(),
            'id' => $userId,
        ]);
    }

    private function clearFailedLoginState(int $userId): void
    {
        $stmt = $this->pdo()->prepare(
            'UPDATE users SET login_attempts = 0, locked_until = NULL, updated_at = :updated_at WHERE id = :id'
        );
        $stmt->execute([
            'updated_at' => $this->now(),
            'id' => $userId,
        ]);
    }

    private function updateLastLogin(int $userId): void
    {
        $stmt = $this->pdo()->prepare(
            'UPDATE users SET last_login = :last_login, updated_at = :updated_at WHERE id = :id'
        );
        $now = $this->now();
        $stmt->execute([
            'last_login' => $now,
            'updated_at' => $now,
            'id' => $userId,
        ]);
    }

    private function validatePasswordPolicy(string $password): void
    {
        $errors = [];
        $policy = config('auth.passwords', []);
        $minLength = (int) ($policy['min_length'] ?? 8);

        if (strlen($password) < $minLength) {
            $errors[] = "Password must be at least {$minLength} characters long.";
        }

        if (($policy['require_uppercase'] ?? true) && !preg_match('/[A-Z]/', $password)) {
            $errors[] = 'Password must contain at least one uppercase letter.';
        }

        if (($policy['require_lowercase'] ?? true) && !preg_match('/[a-z]/', $password)) {
            $errors[] = 'Password must contain at least one lowercase letter.';
        }

        if (($policy['require_number'] ?? true) && !preg_match('/[0-9]/', $password)) {
            $errors[] = 'Password must contain at least one number.';
        }

        if (($policy['require_special'] ?? true) && !preg_match('/[^A-Za-z0-9]/', $password)) {
            $errors[] = 'Password must contain at least one special character.';
        }

        if ($errors) {
            throw new AuthException('Password does not meet policy requirements', 422, [
                'password' => implode(' ', $errors),
            ]);
        }
    }

    private function publicUser(array $user): array
    {
        return [
            'id' => (int) $user['id'],
            'name' => $user['name'],
            'email' => $user['email'],
            'phone' => $user['phone'],
            'role' => $user['role'],
            'is_active' => (bool) $user['is_active'],
            'is_verified' => (bool) $user['is_verified'],
        ];
    }

    private function selectDeliveryChannel(array $user, string $preferredChannel): string
    {
        $preferred = strtolower(trim($preferredChannel));

        if (in_array($preferred, ['email', 'phone'], true) && !empty($user[$preferred])) {
            return $preferred;
        }

        if (!empty($user['phone'])) {
            return 'phone';
        }

        if (!empty($user['email'])) {
            return 'email';
        }

        throw new AuthException('No OTP delivery channel available', 422, [
            'channel' => 'The user does not have a valid email or phone destination.',
        ]);
    }

    private function roleId(string $role): int
    {
        $stmt = $this->pdo()->prepare('SELECT id FROM roles WHERE name = :name LIMIT 1');
        $stmt->execute(['name' => $role]);
        $id = $stmt->fetchColumn();

        if (!$id) {
            throw new AuthException('Role not configured', 500, [
                'role' => "The role '{$role}' is missing from the roles table.",
            ]);
        }

        return (int) $id;
    }

    private function normalizeRole(mixed $role): ?string
    {
        if (!is_string($role) || trim($role) === '') {
            return null;
        }

        $normalized = strtolower(trim($role));

        if (!in_array($normalized, config('auth.roles', []), true)) {
            throw new AuthException('Unsupported role', 422, [
                'role' => 'The supplied role is not supported.',
            ]);
        }

        return $normalized;
    }

    private function normalizePurpose(mixed $purpose): string
    {
        $normalized = strtolower(trim((string) $purpose));

        return match ($normalized) {
            'login', 'password_reset' => $normalized,
            'forgot_password' => 'password_reset',
            default => throw new AuthException('Unsupported OTP purpose', 422, [
                'purpose' => 'The supplied OTP purpose is not supported.',
            ]),
        };
    }

    private function normalizeDeviceType(string $type): string
    {
        $normalized = strtolower(trim($type));

        return in_array($normalized, ['android', 'ios', 'web'], true) ? $normalized : 'web';
    }

    private function normalizeNullableString(mixed $value): ?string
    {
        if (!is_string($value)) {
            return null;
        }

        $normalized = trim($value);

        return $normalized === '' ? null : $normalized;
    }

    private function shouldExposeSensitiveDebugData(): bool
    {
        return in_array(config('app.env'), ['local', 'testing'], true)
            || filter_var(config('app.debug', false), FILTER_VALIDATE_BOOL);
    }

    private function generateOtp(): string
    {
        $length = max(4, (int) config('auth.otp.length', 6));
        $max = (10 ** $length) - 1;

        return str_pad((string) random_int(0, $max), $length, '0', STR_PAD_LEFT);
    }

    private function now(): string
    {
        return (new DateTimeImmutable())->format('Y-m-d H:i:s');
    }

    private function nowPlusSeconds(int $seconds): string
    {
        return (new DateTimeImmutable())
            ->add(new DateInterval('PT' . max($seconds, 0) . 'S'))
            ->format('Y-m-d H:i:s');
    }

    private function nowMinusSeconds(int $seconds): string
    {
        return (new DateTimeImmutable())
            ->sub(new DateInterval('PT' . max($seconds, 0) . 'S'))
            ->format('Y-m-d H:i:s');
    }

    private function futureTimestamp(?string $timestamp): bool
    {
        return $timestamp !== null && strtotime($timestamp) > time();
    }

    private function pdo(): PDO
    {
        return $this->connection ?? Database::connection();
    }

    private function otpDelivery(): OtpDeliveryService
    {
        return $this->otpDeliveryService ?? new OtpDeliveryService();
    }
}
