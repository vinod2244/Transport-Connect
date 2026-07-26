<?php

return [
    'default' => env('PAYMENT_GATEWAY', 'razorpay'),

    'razorpay' => [
        'key_id' => env('RAZORPAY_KEY_ID'),
        'key_secret' => env('RAZORPAY_KEY_SECRET'),
        'webhook_secret' => env('RAZORPAY_WEBHOOK_SECRET'),
        'currency' => 'INR',
        'timeout' => 30,
    ],

    'phonepe' => [
        'merchant_id' => env('PHONEPE_MERCHANT_ID'),
        'api_key' => env('PHONEPE_API_KEY'),
        'salt_key' => env('PHONEPE_SALT_KEY'),
        'environment' => env('PHONEPE_ENVIRONMENT', 'UAT'),
        'redirect_url' => env('APP_URL') . '/api/payments/phonepe/callback',
    ],

    'methods' => [
        'card' => true,
        'upi' => true,
        'wallet' => true,
        'netbanking' => true,
        'cod' => true,
    ],
];
