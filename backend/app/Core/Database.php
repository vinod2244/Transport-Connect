<?php

namespace App\Core;

use PDO;

final class Database
{
    private static ?PDO $connection = null;

    public static function connection(): PDO
    {
        if (self::$connection instanceof PDO) {
            return self::$connection;
        }

        $driver = config('database.driver', 'mysql');
        $options = [
            PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
            PDO::ATTR_EMULATE_PREPARES => false,
        ];

        if ($driver === 'sqlite') {
            $database = config('database.database', ':memory:');
            $dsn = 'sqlite:' . $database;
            self::$connection = new PDO($dsn, null, null, $options);
            return self::$connection;
        }

        $dsn = sprintf(
            'mysql:host=%s;port=%s;dbname=%s;charset=%s',
            config('database.host', '127.0.0.1'),
            config('database.port', 3306),
            config('database.database', ''),
            config('database.charset', 'utf8mb4')
        );

        $mysqlOptions = $options + [
            PDO::MYSQL_ATTR_INIT_COMMAND => sprintf(
                "SET NAMES '%s' COLLATE '%s'",
                config('database.charset', 'utf8mb4'),
                config('database.collation', 'utf8mb4_unicode_ci')
            ),
        ];

        self::$connection = new PDO(
            $dsn,
            config('database.username', ''),
            config('database.password', ''),
            $mysqlOptions
        );

        return self::$connection;
    }

    public static function setConnection(PDO $connection): void
    {
        self::$connection = $connection;
    }

    public static function reset(): void
    {
        self::$connection = null;
    }
}
