<?php

return [
    'name' => env('APP_NAME', 'APTransportConnect'),
    'env' => env('APP_ENV', 'production'),
    'debug' => env('APP_DEBUG', false),
    'url' => env('APP_URL', 'http://localhost:8000'),
    'timezone' => env('SERVER_TIMEZONE', 'UTC'),

    'providers' => [
        'database',
        'cache',
        'mail',
        'firebase',
        'payment',
    ],

    'api' => [
        'version' => env('API_VERSION', 'v1'),
        'prefix' => '/api',
        'rate_limit' => [
            'enabled' => true,
            'requests' => env('API_RATE_LIMIT_REQUESTS', 100),
            'period' => env('API_RATE_LIMIT_PERIOD', 60),
        ],
    ],

    'upload' => [
        'max_size' => env('MAX_UPLOAD_SIZE', 10485760),
        'allowed_types' => explode(',', env('ALLOWED_FILE_TYPES', 'jpg,jpeg,png,pdf')),
        'directory' => env('UPLOAD_DIR', 'public/uploads/'),
    ],

    'security' => [
        'cors_enabled' => true,
        'csrf_protection' => env('CSRF_TOKEN_ENABLED', true),
        'xss_protection' => env('XSS_PROTECTION_ENABLED', true),
    ],
];
