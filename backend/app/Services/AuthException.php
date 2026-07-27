<?php

namespace App\Services;

use RuntimeException;

class AuthException extends RuntimeException
{
    public function __construct(
        string $message,
        private readonly int $status = 400,
        private readonly array $errors = []
    ) {
        parent::__construct($message);
    }

    public function status(): int
    {
        return $this->status;
    }

    public function errors(): array
    {
        return $this->errors;
    }
}
