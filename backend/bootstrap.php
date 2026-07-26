<?php

require __DIR__ . '/vendor/autoload.php';

use Dotenv\Dotenv;

// Load environment variables
$dotenv = Dotenv::createImmutable(__DIR__);
$dotenv->load();

// Define application constants
define('BASE_PATH', __DIR__);
define('APP_PATH', BASE_PATH . '/app');
define('CONFIG_PATH', BASE_PATH . '/config');
define('STORAGE_PATH', BASE_PATH . '/storage');
define('PUBLIC_PATH', BASE_PATH . '/public');
define('UPLOAD_PATH', PUBLIC_PATH . '/uploads');

// Set error reporting
error_reporting(E_ALL);
ini_set('display_errors', env('APP_DEBUG', false) ? 1 : 0);
ini_set('log_errors', 1);
ini_set('error_log', STORAGE_PATH . '/logs/error.log');

// Set timezone
date_default_timezone_set(env('SERVER_TIMEZONE', 'UTC'));

// Start session if needed
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

// Helper function to get environment variable
if (!function_exists('env')) {
    function env(string $key, mixed $default = null): mixed {
        return $_ENV[$key] ?? $default;
    }
}

// Helper function to get config
if (!function_exists('config')) {
    function config(string $key, mixed $default = null): mixed {
        $parts = explode('.', $key);
        $file = array_shift($parts);
        $path = CONFIG_PATH . '/' . $file . '.php';
        
        if (!file_exists($path)) {
            return $default;
        }
        
        $config = require $path;
        
        foreach ($parts as $part) {
            $config = $config[$part] ?? $default;
        }
        
        return $config;
    }
}

// Helper function for logging
if (!function_exists('logger')) {
    function logger() {
        static $logger;
        
        if (!$logger) {
            $logger = new \Monolog\Logger('app');
            $handler = new \Monolog\Handler\StreamHandler(
                STORAGE_PATH . '/logs/app.log',
                \Monolog\Level::Debug
            );
            $logger->pushHandler($handler);
        }
        
        return $logger;
    }
}

// Helper function for response
if (!function_exists('response')) {
    function response(int $status = 200, array $data = [], ?string $message = null) {
        return [
            'status' => $status,
            'message' => $message,
            'data' => $data,
            'timestamp' => date('Y-m-d H:i:s'),
        ];
    }
}

// Helper function for error response
if (!function_exists('error_response')) {
    function error_response(int $status = 400, string $message = 'Error', array $errors = []) {
        return [
            'status' => $status,
            'message' => $message,
            'errors' => $errors,
            'timestamp' => date('Y-m-d H:i:s'),
        ];
    }
}

return [
    'app' => config('app'),
    'database' => config('database'),
    'jwt' => config('jwt'),
    'payment' => config('payment'),
    'firebase' => config('firebase'),
];
