<?php

if (file_exists(__DIR__ . '/vendor/autoload.php')) {
    require __DIR__ . '/vendor/autoload.php';
} else {
    spl_autoload_register(static function (string $class): void {
        $prefixes = [
            'App\\' => __DIR__ . '/app/',
            'Tests\\' => __DIR__ . '/tests/',
        ];

        foreach ($prefixes as $prefix => $baseDir) {
            if (!str_starts_with($class, $prefix)) {
                continue;
            }

            $relativeClass = substr($class, strlen($prefix));
            $path = $baseDir . str_replace('\\', '/', $relativeClass) . '.php';

            if (file_exists($path)) {
                require $path;
            }
        }
    });
}

// Load environment variables
$envPath = __DIR__ . '/.env';
if (file_exists($envPath)) {
    $lines = file($envPath, FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES) ?: [];
    foreach ($lines as $line) {
        $trimmed = trim($line);
        if ($trimmed === '' || str_starts_with($trimmed, '#') || !str_contains($trimmed, '=')) {
            continue;
        }

        [$key, $value] = explode('=', $trimmed, 2);
        $key = trim($key);
        $value = trim($value);

        if (
            (str_starts_with($value, '"') && str_ends_with($value, '"'))
            || (str_starts_with($value, "'") && str_ends_with($value, "'"))
        ) {
            $value = substr($value, 1, -1);
        }

        $_ENV[$key] = $value;
        $_SERVER[$key] = $value;
    }
}

// Helper function to get environment variable
if (!function_exists('env')) {
    function env(string $key, mixed $default = null): mixed {
        return $_ENV[$key] ?? $default;
    }
}

// Define application constants
define('BASE_PATH', __DIR__);
define('APP_PATH', BASE_PATH . '/app');
define('CONFIG_PATH', BASE_PATH . '/config');
define('STORAGE_PATH', BASE_PATH . '/storage');
define('PUBLIC_PATH', BASE_PATH . '/public');
define('UPLOAD_PATH', PUBLIC_PATH . '/uploads');

foreach ([STORAGE_PATH, STORAGE_PATH . '/logs', STORAGE_PATH . '/cache'] as $directory) {
    if (!is_dir($directory)) {
        mkdir($directory, 0777, true);
    }
}

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
            if (class_exists(\Monolog\Logger::class) && class_exists(\Monolog\Handler\StreamHandler::class)) {
                $logger = new \Monolog\Logger('app');
                $handler = new \Monolog\Handler\StreamHandler(
                    STORAGE_PATH . '/logs/app.log',
                    \Monolog\Level::Debug
                );
                $logger->pushHandler($handler);
            } else {
                $logger = new class {
                    public function info(string $message, array $context = []): void
                    {
                        $this->write('INFO', $message, $context);
                    }

                    public function error(string $message, array $context = []): void
                    {
                        $this->write('ERROR', $message, $context);
                    }

                    private function write(string $level, string $message, array $context = []): void
                    {
                        $line = sprintf(
                            "[%s] %s %s %s\n",
                            date('Y-m-d H:i:s'),
                            $level,
                            $message,
                            $context ? json_encode($context) : ''
                        );
                        file_put_contents(STORAGE_PATH . '/logs/app.log', $line, FILE_APPEND);
                    }
                };
            }
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
    'auth' => config('auth'),
    'payment' => config('payment'),
    'firebase' => config('firebase'),
];
