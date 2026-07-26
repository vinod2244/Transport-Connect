<?php

namespace App\Helpers;

class ValidationHelper
{
    private array $errors = [];

    public function required(string $field, string $value): self
    {
        if (empty(trim($value))) {
            $this->errors[$field] = "$field is required";
        }
        return $this;
    }

    public function email(string $field, string $value): self
    {
        if (!filter_var($value, FILTER_VALIDATE_EMAIL)) {
            $this->errors[$field] = "$field must be a valid email";
        }
        return $this;
    }

    public function phone(string $field, string $value): self
    {
        if (!preg_match('/^[0-9]{10}$/', $value)) {
            $this->errors[$field] = "$field must be a valid phone number";
        }
        return $this;
    }

    public function minLength(string $field, string $value, int $length): self
    {
        if (strlen($value) < $length) {
            $this->errors[$field] = "$field must be at least $length characters";
        }
        return $this;
    }

    public function maxLength(string $field, string $value, int $length): self
    {
        if (strlen($value) > $length) {
            $this->errors[$field] = "$field must not exceed $length characters";
        }
        return $this;
    }

    public function isValid(): bool
    {
        return empty($this->errors);
    }

    public function getErrors(): array
    {
        return $this->errors;
    }
}
