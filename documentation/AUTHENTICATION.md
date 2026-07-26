# JWT Authentication Guide

## Overview

AP Transport Connect uses JSON Web Tokens (JWT) for stateless authentication. Each token contains encoded user information and is signed with a secret key.

## Token Structure

JWT consists of three parts separated by dots (.): header.payload.signature

### Header
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

### Payload (Claims)
```json
{
  "iss": "http://localhost:8000",
  "aud": "APTransportConnect",
  "iat": 1690000000,
  "exp": 1690086400,
  "nbf": 1690000000,
  "jti": "unique_token_id",
  "user_id": 1,
  "phone": "9876543210",
  "role": "customer",
  "email": "user@example.com"
}
```

### Signature
```
HMAC-SHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret_key
)
```

## Token Generation

### Login Flow

1. User provides credentials (phone + password or phone + OTP)
2. Server validates credentials
3. Server generates JWT token with 24-hour expiry
4. Server generates refresh token with 7-day expiry
5. Both tokens returned to client

### Token Claims

- **iss** (Issuer): Application URL
- **aud** (Audience): Application name
- **iat** (Issued At): Token creation time (Unix timestamp)
- **exp** (Expiration): Token expiration time (86400 seconds = 24 hours)
- **nbf** (Not Before): Token validity start time
- **jti** (JWT ID): Unique token identifier
- **user_id**: User identifier
- **phone**: User phone number
- **role**: User role (customer, driver, owner, admin)
- **email**: User email

## Token Usage

### Include Token in Requests

```bash
GET /api/user/profile HTTP/1.1
Host: api.aptransportconnect.com
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json
```

### Extracting Token from Header

The authorization header format:
```
Authorization: Bearer <token>
```

The server extracts and validates:
1. Check "Bearer " prefix
2. Extract token after prefix
3. Decode and verify signature
4. Check expiration
5. Validate claims

## Token Refresh

### Refresh Token Endpoint

```
POST /api/auth/refresh
Content-Type: application/json

{
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Response

```json
{
  "status": 200,
  "message": "Token refreshed",
  "data": {
    "token": "new_jwt_token",
    "expires_in": 86400
  }
}
```

## Token Lifecycle

### Access Token
- **Duration**: 24 hours
- **Usage**: API requests
- **Storage**: In-memory or secure storage
- **Expiration**: Automatic after 24 hours

### Refresh Token
- **Duration**: 7 days
- **Usage**: Getting new access token
- **Storage**: Secure storage (encrypted)
- **Rotation**: New refresh token on each refresh

## Security Considerations

### Token Storage

**Android Apps:**
- Use encrypted SharedPreferences
- Or use Secure Enclave (Android 6+)
- Never store in plain text

**Web:**
- Use httpOnly cookies (recommended)
- Or secure localStorage with encryption
- Never expose to JavaScript

### Token Transmission

- Always use HTTPS
- Include in Authorization header
- Never include in URL parameters
- Never include in POST body

### Token Validation

Server validates:
1. Signature matches (using secret key)
2. Token not expired
3. Claims are valid
4. Token not revoked
5. User still exists and is active

## Device Management

### Token Binding

Tokens can be bound to specific devices:
- Device ID stored in token
- Request must come from same device
- Prevents token theft/sharing

### Multi-Device Support

- User can be logged in on multiple devices
- Each device gets separate token
- Tokens tracked in user_devices table
- User can logout from specific device

## Logout

### Logout Flow

```
POST /api/auth/logout
Authorization: Bearer <jwt_token>
```

### Server Actions

1. Mark device token as inactive
2. Invalidate token if token blacklist used
3. Clear refresh token
4. Update last logout time

## Error Handling

### Invalid Token

```json
{
  "status": 401,
  "message": "Invalid token",
  "errors": {
    "auth": "Token signature is invalid"
  }
}
```

### Expired Token

```json
{
  "status": 401,
  "message": "Token expired",
  "errors": {
    "auth": "Token has expired. Please refresh or login again."
  }
}
```

### Missing Token

```json
{
  "status": 401,
  "message": "Missing authentication token",
  "errors": {
    "auth": "Authorization header is missing"
  }
}
```

## Implementation Examples

### PHP (Backend)

```php
use Firebase\JWT\JWT;
use Firebase\JWT\Key;

// Encode
$payload = [
    'user_id' => 1,
    'phone' => '9876543210',
    'role' => 'customer',
    'iat' => time(),
    'exp' => time() + 86400
];

$token = JWT::encode(
    $payload,
    config('jwt.secret'),
    config('jwt.algorithm')
);

// Decode
$decoded = JWT::decode(
    $token,
    new Key(config('jwt.secret'), config('jwt.algorithm'))
);

$user_id = $decoded->user_id;
```

### Kotlin (Android)

```kotlin
// Add token to requests
interceptor.addInterceptor { chain ->
    val original = chain.request()
    val token = PreferenceManager.getToken()
    
    val request = original.newBuilder()
        .header("Authorization", "Bearer $token")
        .build()
    
    chain.proceed(request)
}

// Handle expired token
if (response.code == 401) {
    // Refresh token
    val newToken = authRepository.refreshToken()
    saveToken(newToken)
    // Retry request
}
```

## Token Blacklist (Optional)

For additional security, maintain token blacklist:

```sql
CREATE TABLE token_blacklist (
    id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    token VARCHAR(500) NOT NULL,
    user_id INT UNSIGNED NOT NULL,
    reason VARCHAR(100),
    blacklisted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_token (token),
    INDEX idx_blacklisted_at (blacklisted_at)
);
```

Check blacklist on every request.

## Best Practices

1. **Use HTTPS only** - Never transmit tokens over HTTP
2. **Keep secret secure** - Protect JWT_SECRET in environment
3. **Set short expiry** - 24 hours for access token
4. **Use refresh tokens** - Rotate access tokens frequently
5. **Validate on server** - Always validate token on server
6. **Handle refresh** - Implement automatic token refresh
7. **Secure storage** - Use encrypted storage on clients
8. **Monitor tokens** - Log token generation and usage
9. **Implement logout** - Support logout with token invalidation
10. **Support multi-device** - Track tokens per device
