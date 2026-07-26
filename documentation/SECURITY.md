# Security Best Practices

## Overview

AP Transport Connect implements industry-standard security practices to protect user data and ensure platform integrity.

## 1. Authentication Security

### JWT Implementation

- **Algorithm**: HS256 (HMAC-SHA256)
- **Secret Key**: Minimum 32 characters, stored in environment variables
- **Token Expiry**: 24 hours for access token
- **Refresh Token**: 7 days, rotated on each refresh
- **Token Signing**: Server-side only, never trust client-generated tokens

### Password Security

- **Hashing**: bcrypt with 10+ salt rounds
- **Minimum Requirements**:
  - Length: 8 characters minimum
  - Complexity: Mix of uppercase, lowercase, numbers, special characters
  - Not in compromised password databases

### OTP Security

- **Length**: 6 digits
- **Expiry**: 10 minutes
- **Attempts**: Maximum 3 failed attempts before rate limiting
- **Regeneration**: Single-use OTP, cannot be reused

## 2. Data Protection

### Encryption at Rest

```php
// Encrypt sensitive data
$encrypted = openssl_encrypt(
    $plaintext,
    'AES-256-CBC',
    hash('sha256', $encryption_key),
    0,
    $iv
);
```

### Encryption in Transit

- **HTTPS Only**: All API requests must use TLS 1.2+
- **Certificate Pinning**: Optional for mobile apps
- **HSTS Header**: Enforce HTTPS

### Sensitive Data

- **Never Log**:
  - Passwords
  - JWT tokens
  - OTP codes
  - Credit card numbers
  - Bank account details

- **Encrypt**:
  - Phone numbers (in logs)
  - Email addresses (in logs)
  - Bank account info
  - Payment transaction details

## 3. SQL Injection Prevention

### Prepared Statements (Always)

```php
// Bad - Vulnerable to SQL injection
$query = "SELECT * FROM users WHERE phone = '$phone'";

// Good - Using prepared statements
$stmt = $pdo->prepare("SELECT * FROM users WHERE phone = ?");
$stmt->execute([$phone]);
$user = $stmt->fetch();
```

### Parameter Binding

```php
// Named parameters
$stmt = $pdo->prepare("SELECT * FROM users WHERE email = :email");
$stmt->execute([':email' => $email]);

// Positional parameters
$stmt = $pdo->prepare("SELECT * FROM users WHERE email = ?");
$stmt->execute([$email]);
```

## 4. XSS (Cross-Site Scripting) Prevention

### Output Encoding

```php
// HTML escape output
echo htmlspecialchars($user_input, ENT_QUOTES, 'UTF-8');

// JSON encode for JSON responses
header('Content-Type: application/json');
echo json_encode($data);
```

### Content Security Policy

```php
header("Content-Security-Policy: default-src 'self'; script-src 'self' https://cdn.jsdelivr.net");
```

## 5. CSRF (Cross-Site Request Forgery) Protection

### CSRF Token

```php
// Generate token
$csrf_token = bin2hex(random_bytes(32));
$_SESSION['csrf_token'] = $csrf_token;

// Validate on POST/PUT/DELETE
if ($_POST['csrf_token'] !== $_SESSION['csrf_token']) {
    http_response_code(403);
    exit('CSRF token validation failed');
}
```

### SameSite Cookie

```php
header('Set-Cookie: session=value; SameSite=Strict; Secure; HttpOnly');
```

## 6. Access Control

### Role-Based Access Control (RBAC)

```php
// Check user role
if ($user->role !== 'admin') {
    http_response_code(403);
    exit('Access denied');
}

// Or check specific permission
if (!$user->hasPermission('manage_drivers')) {
    http_response_code(403);
    exit('Access denied');
}
```

### Middleware Authentication

```php
public function authenticate(Request $request): void
{
    $token = $this->getTokenFromHeader($request);
    
    if (!$token || !JWTHelper::isValid($token)) {
        http_response_code(401);
        exit(json_encode(['message' => 'Unauthorized']));
    }
    
    $payload = JWTHelper::decode($token);
    $request->user_id = $payload['user_id'];
    $request->user_role = $payload['role'];
}
```

## 7. Rate Limiting

### Throttling

```php
class RateLimitMiddleware
{
    private const REQUESTS_PER_MINUTE = 100;
    
    public function check($user_id): bool
    {
        $key = "rate_limit:{$user_id}";
        $count = redis()->get($key) ?? 0;
        
        if ($count >= self::REQUESTS_PER_MINUTE) {
            http_response_code(429);
            return false;
        }
        
        redis()->incr($key);
        redis()->expire($key, 60);
        return true;
    }
}
```

## 8. Input Validation

### Whitelist Validation

```php
$validator = new ValidationHelper();

$validator
    ->required('phone', $phone)
    ->phone('phone', $phone)
    ->required('email', $email)
    ->email('email', $email)
    ->minLength('password', $password, 8)
    ->maxLength('name', $name, 100);

if (!$validator->isValid()) {
    http_response_code(422);
    exit(json_encode($validator->getErrors()));
}
```

### Sanitization

```php
// Remove HTML tags
$text = strip_tags($user_input);

// Trim whitespace
$text = trim($user_input);

// Validate email format
filter_var($email, FILTER_VALIDATE_EMAIL);

// Validate phone
preg_match('/^[0-9]{10}$/', $phone);
```

## 9. API Security

### CORS Headers

```php
header('Access-Control-Allow-Origin: https://example.com');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');
header('Access-Control-Max-Age: 3600');
```

### API Versioning

```
/api/v1/bookings
/api/v2/bookings
```

### Request/Response Validation

```php
// Validate content type
if ($_SERVER['CONTENT_TYPE'] !== 'application/json') {
    http_response_code(400);
    exit('Invalid content type');
}

// Validate JSON
$data = json_decode(file_get_contents('php://input'), true);
if (json_last_error() !== JSON_ERROR_NONE) {
    http_response_code(400);
    exit('Invalid JSON');
}
```

## 10. Mobile Security (Android)

### Secure Storage

```kotlin
// Use EncryptedSharedPreferences
val encryptedSharedPreferences = EncryptedSharedPreferences.create(
    context,
    "app_prefs",
    MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
)

// Store sensitive data
encryptedSharedPreferences.edit().putString("jwt_token", token).apply()
```

### Certificate Pinning

```kotlin
val pins = arrayOf(
    "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="
)

val certificatePinner = CertificatePinner.Builder()
    .add("api.aptransportconnect.com", *pins)
    .build()

val httpClient = OkHttpClient.Builder()
    .certificatePinner(certificatePinner)
    .build()
```

### Biometric Authentication

```kotlin
val biometricPrompt = BiometricPrompt(activity,
    object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(
            result: BiometricPrompt.AuthenticationResult
        ) {
            // Authenticate user
        }
    }
)

biometricPrompt.authenticate(promptInfo)
```

## 11. Logging & Monitoring

### Secure Logging

```php
// Log with sensitive data redacted
logger()->info('User login', [
    'user_id' => $user_id,
    'phone' => substr($phone, 0, 3) . '****' . substr($phone, -2),
    'ip_address' => $_SERVER['REMOTE_ADDR'],
    'timestamp' => date('Y-m-d H:i:s')
]);
```

### Audit Trail

```php
AuditLog::create([
    'user_id' => $user_id,
    'action' => 'booking_created',
    'resource_type' => 'booking',
    'resource_id' => $booking_id,
    'old_values' => null,
    'new_values' => $booking->toJson(),
    'ip_address' => $_SERVER['REMOTE_ADDR'],
    'user_agent' => $_SERVER['HTTP_USER_AGENT']
]);
```

## 12. Deployment Security

### Environment Variables

```bash
# Never commit .env file
echo ".env" >> .gitignore

# Use strong secrets
JWT_SECRET=$(openssl rand -base64 32)
DB_PASSWORD=$(openssl rand -hex 16)
RAZORPAY_KEY=$(openssl rand -hex 32)
```

### SSL/TLS Certificate

```bash
# Generate self-signed certificate (development)
openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -days 365

# Use Let's Encrypt (production)
certbot certonly --standalone -d api.aptransportconnect.com
```

### Firewall Rules

```bash
# Allow only necessary ports
sudo ufw allow 22/tcp      # SSH
sudo ufw allow 80/tcp      # HTTP
sudo ufw allow 443/tcp     # HTTPS
sudo ufw allow 3306/tcp    # MySQL (from app server only)
sudo ufw default deny incoming
```

## 13. Compliance

### Data Privacy (GDPR/Local Laws)

- Obtain user consent before collecting data
- Provide data export functionality
- Implement right to be forgotten
- Encrypt personal data
- Regular security audits

### PCI DSS (Payment Card Industry Data Security Standard)

- Never store full credit card numbers
- Use tokenization for payments
- Implement 3D Secure
- Regular security assessments
- Maintain audit logs

## 14. Security Checklist

- [ ] All passwords hashed with bcrypt
- [ ] JWT tokens signed and validated
- [ ] HTTPS enforced
- [ ] SQL injection prevention (prepared statements)
- [ ] XSS protection (HTML encoding)
- [ ] CSRF tokens implemented
- [ ] Rate limiting enabled
- [ ] RBAC implemented
- [ ] Audit logging enabled
- [ ] Sensitive data encrypted
- [ ] Environment variables used for secrets
- [ ] Regular security updates
- [ ] Dependency vulnerabilities checked
- [ ] Code review process in place
- [ ] Incident response plan defined

## 15. Security Testing

### Automated Testing

```bash
# Check for vulnerable dependencies
composer audit
npm audit

# Static code analysis
phpstan analyse app/
scanjs -r .

# Dependency scanning
retire --js --jspath node_modules
```

### Manual Testing

- Penetration testing
- SQL injection attempts
- XSS payloads
- CSRF validation
- Authentication bypass
- Authorization bypass
- Data exposure

## 16. Incident Response

### Steps

1. **Detect**: Monitor logs and alerts
2. **Isolate**: Disconnect affected systems
3. **Contain**: Prevent further damage
4. **Eradicate**: Remove threat
5. **Recover**: Restore normal operations
6. **Lessons Learned**: Document and improve

### Contact

Security incidents: security@aptransportconnect.com
