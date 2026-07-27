<?php

return [
    'roles' => ['customer', 'driver', 'owner', 'admin'],

    'access_token_ttl' => (int) env('JWT_ACCESS_TOKEN_EXPIRY', env('JWT_EXPIRY', 3600)),
    'refresh_token_ttl' => (int) env('JWT_REFRESH_TOKEN_EXPIRY', env('JWT_REFRESH_EXPIRY', 604800)),
    'remember_me_refresh_token_ttl' => (int) env('JWT_REMEMBER_ME_REFRESH_TOKEN_EXPIRY', 2592000),
    'password_reset_grant_ttl' => (int) env('PASSWORD_RESET_GRANT_EXPIRY', 900),

    'otp' => [
        'length' => (int) env('OTP_LENGTH', 6),
        'expiry' => (int) env('OTP_EXPIRY', 300),
        'cooldown' => (int) env('OTP_RESEND_COOLDOWN', 60),
        'max_attempts' => (int) env('OTP_MAX_ATTEMPTS', 5),
        'max_requests_per_hour' => (int) env('OTP_MAX_REQUESTS_PER_HOUR', 5),
    ],

    'passwords' => [
        'min_length' => (int) env('PASSWORD_MIN_LENGTH', 8),
        'require_uppercase' => filter_var(env('PASSWORD_REQUIRE_UPPERCASE', true), FILTER_VALIDATE_BOOL),
        'require_lowercase' => filter_var(env('PASSWORD_REQUIRE_LOWERCASE', true), FILTER_VALIDATE_BOOL),
        'require_number' => filter_var(env('PASSWORD_REQUIRE_NUMBER', true), FILTER_VALIDATE_BOOL),
        'require_special' => filter_var(env('PASSWORD_REQUIRE_SPECIAL', true), FILTER_VALIDATE_BOOL),
    ],

    'login' => [
        'max_attempts' => (int) env('LOGIN_MAX_ATTEMPTS', 5),
        'lockout_seconds' => (int) env('LOGIN_LOCKOUT_SECONDS', 900),
    ],
];
