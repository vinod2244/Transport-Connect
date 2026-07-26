<?php

return [
    'secret' => env('JWT_SECRET', 'your-secret-key'),
    'algorithm' => env('JWT_ALGORITHM', 'HS256'),
    'expiry' => env('JWT_EXPIRY', 86400), // 24 hours
    'refresh_expiry' => env('JWT_REFRESH_EXPIRY', 604800), // 7 days
    'issuer' => env('APP_URL', 'http://localhost:8000'),
    'audience' => env('APP_NAME', 'APTransportConnect'),

    'claims' => [
        'iss' => env('APP_URL'),
        'aud' => env('APP_NAME'),
        'iat' => true,
        'exp' => true,
        'nbf' => true,
        'jti' => true,
    ],
];
