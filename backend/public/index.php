<?php

use App\Core\ApiApplication;
use App\Core\Request;

// Bootstrap application
require __DIR__ . '/../bootstrap.php';

// Enable CORS headers
header('Access-Control-Allow-Origin: ' . env('CORS_ALLOWED_ORIGINS', '*'));
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS, PATCH');
header('Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With');
header('Content-Type: application/json; charset=utf-8');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

$application = new ApiApplication();
[$statusCode, $payload] = $application->handle(Request::capture());

http_response_code($statusCode);
echo json_encode($payload);
