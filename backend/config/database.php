<?php

$driver = env('DB_CONNECTION', 'mysql');
$charset = env('DB_CHARSET', 'utf8mb4');
$collation = env('DB_COLLATION', 'utf8mb4_unicode_ci');

$options = [
    PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
    PDO::ATTR_EMULATE_PREPARES => false,
];

if ($driver === 'mysql') {
    $options[PDO::MYSQL_ATTR_INIT_COMMAND] = "SET NAMES '{$charset}' COLLATE '{$collation}'";
}

return [
    'driver' => $driver,
    'host' => env('DB_HOST', 'localhost'),
    'port' => env('DB_PORT', 3306),
    'database' => env('DB_DATABASE', env('DB_NAME', BASE_PATH . '/storage/database.sqlite')),
    'username' => env('DB_USERNAME', env('DB_USER', 'root')),
    'password' => env('DB_PASSWORD', ''),
    'charset' => $charset,
    'collation' => $collation,
    'options' => $options,

    'pool' => [
        'enabled' => $driver === 'mysql',
        'min_connections' => 5,
        'max_connections' => 20,
        'idle_timeout' => 300,
    ],
];
