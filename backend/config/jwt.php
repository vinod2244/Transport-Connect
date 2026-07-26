<?php

return [
    'secret' => env('JWT_SECRET', 'your-secret-key'),
    'algorithm' => env('JWT_ALGORITHM', 'HS256'),
    'expiry' => (int) env('JWT_ACCESS_TOKEN_EXPIRY', env('JWT_EXPIRY', 3600)),
    'refresh_expiry' => (int) env('JWT_REFRESH_TOKEN_EXPIRY', env('JWT_REFRESH_EXPIRY', 604800)),
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
