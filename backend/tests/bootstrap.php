<?php

$databasePath = sys_get_temp_dir() . '/transport-connect-test.sqlite';
$envPath = dirname(__DIR__) . '/.env';

if (!file_exists($envPath)) {
    file_put_contents($envPath, implode(PHP_EOL, [
        'APP_NAME="AP Transport Connect"',
        'APP_ENV=testing',
        'APP_DEBUG=true',
        'APP_URL=http://localhost',
        'SERVER_TIMEZONE=UTC',
        'DB_CONNECTION=sqlite',
        'DB_DATABASE=' . $databasePath,
        'JWT_SECRET=test-secret-key',
        'JWT_ALGORITHM=HS256',
        'JWT_ACCESS_TOKEN_EXPIRY=3600',
        'JWT_REFRESH_TOKEN_EXPIRY=604800',
        'JWT_REMEMBER_ME_REFRESH_TOKEN_EXPIRY=2592000',
        'OTP_EXPIRY=300',
        'PASSWORD_RESET_GRANT_EXPIRY=900',
    ]) . PHP_EOL);
}

require dirname(__DIR__) . '/bootstrap.php';
