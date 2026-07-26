<?php

declare(strict_types=1);

namespace App\Core;

final class Logger
{
    public static function error(string $message, array $context = []): void
    {
        $line = sprintf(
            "[%s] ERROR: %s %s\n",
            date('Y-m-d H:i:s'),
            $message,
            json_encode($context, JSON_UNESCAPED_UNICODE)
        );
        file_put_contents(__DIR__ . '/../../storage/logs/app.log', $line, FILE_APPEND);
    }
}
