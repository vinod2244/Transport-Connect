<?php

namespace Tests;

use App\Core\ApiApplication;
use App\Core\Database;
use App\Core\Request;
use App\Helpers\JWTHelper;
use PDO;
use PHPUnit\Framework\TestCase;

class AuthIntegrationTest extends TestCase
{
    private ApiApplication $application;

    protected function setUp(): void
    {
        parent::setUp();

        $pdo = new PDO('sqlite::memory:');
        $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        $pdo->setAttribute(PDO::ATTR_DEFAULT_FETCH_MODE, PDO::FETCH_ASSOC);

        Database::setConnection($pdo);
        $this->application = new ApiApplication();

        $this->createSchema($pdo);
        $this->seedRoles($pdo);
        $this->seedUsers($pdo);
    }

    protected function tearDown(): void
    {
        Database::reset();
        parent::tearDown();
    }

    public function testPasswordLoginPersistsRoleAndEnforcesRbac(): void
    {
        [$status, $response] = $this->request('POST', '/auth/login', [
            'identifier' => 'customer@example.com',
            'password' => 'Customer@123',
            'role' => 'customer',
            'device_id' => 'device-customer',
        ]);

        $this->assertSame(200, $status);
        $this->assertSame('customer', $response['data']['user']['role']);

        $payload = JWTHelper::decode($response['data']['access_token']);
        $this->assertSame('customer', $payload['role']);
        $this->assertSame('access', $payload['typ']);
        $this->assertNotEmpty($payload['sid']);

        [$profileStatus, $profileResponse] = $this->request('GET', '/user/profile', [], [
            'Authorization' => 'Bearer ' . $response['data']['access_token'],
        ]);

        $this->assertSame(200, $profileStatus);
        $this->assertSame('customer', $profileResponse['data']['user']['role']);

        [$forbiddenStatus] = $this->request('GET', '/driver/dashboard', [], [
            'Authorization' => 'Bearer ' . $response['data']['access_token'],
        ]);

        $this->assertSame(403, $forbiddenStatus);
    }

    public function testOtpLoginSupportsDriverRoleAndRememberMe(): void
    {
        [$sendStatus, $sendResponse] = $this->request('POST', '/auth/send-otp', [
            'identifier' => '9999999992',
            'role' => 'driver',
            'purpose' => 'login',
            'channel' => 'phone',
        ]);

        $this->assertSame(200, $sendStatus);
        $this->assertArrayHasKey('otp_preview', $sendResponse['data']);

        [$verifyStatus, $verifyResponse] = $this->request('POST', '/auth/verify-otp', [
            'identifier' => '9999999992',
            'role' => 'driver',
            'purpose' => 'login',
            'otp' => $sendResponse['data']['otp_preview'],
            'remember_me' => true,
            'device_id' => 'driver-device',
        ]);

        $this->assertSame(200, $verifyStatus);
        $this->assertSame('driver', $verifyResponse['data']['user']['role']);
        $this->assertGreaterThan(604800, $verifyResponse['data']['refresh_expires_in']);

        [$driverStatus] = $this->request('GET', '/driver/dashboard', [], [
            'Authorization' => 'Bearer ' . $verifyResponse['data']['access_token'],
        ]);

        $this->assertSame(200, $driverStatus);
    }

    public function testRefreshRotationAndLogoutInvalidateSession(): void
    {
        [, $loginResponse] = $this->request('POST', '/auth/login', [
            'identifier' => 'owner@example.com',
            'password' => 'Owner@123',
            'role' => 'owner',
            'device_id' => 'owner-device',
        ]);

        [$refreshStatus, $refreshResponse] = $this->request('POST', '/auth/refresh', [
            'refresh_token' => $loginResponse['data']['refresh_token'],
            'device_id' => 'owner-device',
        ]);

        $this->assertSame(200, $refreshStatus);
        $this->assertNotSame($loginResponse['data']['refresh_token'], $refreshResponse['data']['refresh_token']);

        [$oldRefreshStatus] = $this->request('POST', '/auth/refresh', [
            'refresh_token' => $loginResponse['data']['refresh_token'],
        ]);

        $this->assertSame(401, $oldRefreshStatus);

        [$logoutStatus] = $this->request('POST', '/auth/logout', [], [
            'Authorization' => 'Bearer ' . $refreshResponse['data']['access_token'],
        ]);

        $this->assertSame(200, $logoutStatus);

        [$sessionStatus] = $this->request('GET', '/auth/sessions', [], [
            'Authorization' => 'Bearer ' . $refreshResponse['data']['access_token'],
        ]);

        $this->assertSame(401, $sessionStatus);
    }

    public function testForgotResetAndChangePasswordFlows(): void
    {
        [$forgotStatus, $forgotResponse] = $this->request('POST', '/auth/forgot-password', [
            'identifier' => 'admin@example.com',
            'role' => 'admin',
            'channel' => 'email',
        ]);

        $this->assertSame(200, $forgotStatus);
        $this->assertArrayHasKey('otp_preview', $forgotResponse['data']);

        [$verifyStatus, $verifyResponse] = $this->request('POST', '/auth/verify-password-reset', [
            'identifier' => 'admin@example.com',
            'role' => 'admin',
            'otp' => $forgotResponse['data']['otp_preview'],
        ]);

        $this->assertSame(200, $verifyStatus);

        [$resetStatus] = $this->request('POST', '/auth/reset-password', [
            'reset_token' => $verifyResponse['data']['reset_token'],
            'password' => 'Admin@456',
            'password_confirmation' => 'Admin@456',
        ]);

        $this->assertSame(200, $resetStatus);

        [$oldLoginStatus] = $this->request('POST', '/auth/login', [
            'identifier' => 'admin@example.com',
            'password' => 'Admin@123',
            'role' => 'admin',
        ]);

        $this->assertSame(401, $oldLoginStatus);

        [, $newLoginResponse] = $this->request('POST', '/auth/login', [
            'identifier' => 'admin@example.com',
            'password' => 'Admin@456',
            'role' => 'admin',
            'device_id' => 'admin-device',
        ]);

        [$changeStatus] = $this->request('POST', '/auth/change-password', [
            'current_password' => 'Admin@456',
            'new_password' => 'Admin@789',
            'new_password_confirmation' => 'Admin@789',
        ], [
            'Authorization' => 'Bearer ' . $newLoginResponse['data']['access_token'],
        ]);

        $this->assertSame(200, $changeStatus);

        [$finalLoginStatus] = $this->request('POST', '/auth/login', [
            'identifier' => 'admin@example.com',
            'password' => 'Admin@789',
            'role' => 'admin',
        ]);

        $this->assertSame(200, $finalLoginStatus);
    }

    public function testLogoutAllSessionsRevokesMultipleSessions(): void
    {
        [, $firstLogin] = $this->request('POST', '/auth/login', [
            'identifier' => 'customer@example.com',
            'password' => 'Customer@123',
            'role' => 'customer',
            'device_id' => 'customer-one',
        ]);

        [, $secondLogin] = $this->request('POST', '/auth/login', [
            'identifier' => 'customer@example.com',
            'password' => 'Customer@123',
            'role' => 'customer',
            'device_id' => 'customer-two',
        ]);

        [$logoutAllStatus] = $this->request('POST', '/auth/logout-all', [], [
            'Authorization' => 'Bearer ' . $secondLogin['data']['access_token'],
        ]);

        $this->assertSame(200, $logoutAllStatus);

        [$firstSessionStatus] = $this->request('GET', '/auth/sessions', [], [
            'Authorization' => 'Bearer ' . $firstLogin['data']['access_token'],
        ]);

        $this->assertSame(401, $firstSessionStatus);
    }

    private function request(string $method, string $path, array $body = [], array $headers = []): array
    {
        return $this->application->handle(Request::fromArray($method, $path, $body, $headers));
    }

    private function createSchema(PDO $pdo): void
    {
        $pdo->exec(<<<'SQL'
CREATE TABLE roles (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    email TEXT UNIQUE,
    phone TEXT UNIQUE NOT NULL,
    password_hash TEXT,
    role_id INTEGER NOT NULL,
    profile_image TEXT,
    device_token TEXT,
    is_active INTEGER DEFAULT 1,
    is_verified INTEGER DEFAULT 1,
    is_blocked INTEGER DEFAULT 0,
    last_login TEXT NULL,
    login_attempts INTEGER DEFAULT 0,
    locked_until TEXT NULL,
    created_at TEXT,
    updated_at TEXT,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE auth_sessions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    public_id TEXT NOT NULL UNIQUE,
    device_id TEXT NOT NULL,
    device_name TEXT,
    device_type TEXT NOT NULL,
    ip_address TEXT,
    user_agent TEXT,
    remember_me INTEGER DEFAULT 0,
    last_activity_at TEXT,
    expires_at TEXT NOT NULL,
    revoked_at TEXT NULL,
    created_at TEXT,
    updated_at TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE auth_refresh_tokens (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    session_id INTEGER NOT NULL,
    token_hash TEXT NOT NULL UNIQUE,
    remember_me INTEGER DEFAULT 0,
    expires_at TEXT NOT NULL,
    revoked_at TEXT NULL,
    revoked_reason TEXT NULL,
    created_at TEXT,
    updated_at TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (session_id) REFERENCES auth_sessions(id)
);

CREATE TABLE auth_otps (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    purpose TEXT NOT NULL,
    channel TEXT NOT NULL,
    destination TEXT NOT NULL,
    code_hash TEXT NOT NULL,
    expires_at TEXT NOT NULL,
    attempt_count INTEGER DEFAULT 0,
    consumed_at TEXT NULL,
    created_at TEXT,
    updated_at TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE password_reset_tokens (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    token_hash TEXT NOT NULL UNIQUE,
    source TEXT NOT NULL,
    expires_at TEXT NOT NULL,
    consumed_at TEXT NULL,
    created_at TEXT,
    updated_at TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
SQL);
    }

    private function seedRoles(PDO $pdo): void
    {
        $statement = $pdo->prepare('INSERT INTO roles (name, description) VALUES (:name, :description)');

        foreach (['customer', 'driver', 'owner', 'admin'] as $role) {
            $statement->execute([
                'name' => $role,
                'description' => ucfirst($role) . ' role',
            ]);
        }
    }

    private function seedUsers(PDO $pdo): void
    {
        $users = [
            ['name' => 'Customer User', 'email' => 'customer@example.com', 'phone' => '9999999991', 'password' => 'Customer@123', 'role' => 'customer'],
            ['name' => 'Driver User', 'email' => 'driver@example.com', 'phone' => '9999999992', 'password' => 'Driver@123', 'role' => 'driver'],
            ['name' => 'Owner User', 'email' => 'owner@example.com', 'phone' => '9999999993', 'password' => 'Owner@123', 'role' => 'owner'],
            ['name' => 'Admin User', 'email' => 'admin@example.com', 'phone' => '9999999994', 'password' => 'Admin@123', 'role' => 'admin'],
        ];

        $roleIds = [];
        foreach ($pdo->query('SELECT id, name FROM roles') as $row) {
            $roleIds[$row['name']] = (int) $row['id'];
        }

        $statement = $pdo->prepare(
            'INSERT INTO users (name, email, phone, password_hash, role_id, is_active, is_verified, is_blocked, login_attempts, created_at, updated_at)
             VALUES (:name, :email, :phone, :password_hash, :role_id, 1, 1, 0, 0, :created_at, :updated_at)'
        );

        foreach ($users as $user) {
            $statement->execute([
                'name' => $user['name'],
                'email' => $user['email'],
                'phone' => $user['phone'],
                'password_hash' => password_hash($user['password'], PASSWORD_BCRYPT),
                'role_id' => $roleIds[$user['role']],
                'created_at' => '2026-01-01 00:00:00',
                'updated_at' => '2026-01-01 00:00:00',
            ]);
        }
    }
}
