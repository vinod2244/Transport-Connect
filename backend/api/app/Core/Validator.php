<?php

declare(strict_types=1);

namespace App\Core;

final class Validator
{
    public static function make(array $data, array $rules): void
    {
        $errors = [];

        foreach ($rules as $field => $ruleStr) {
            $ruleList = explode('|', $ruleStr);
            $value = $data[$field] ?? null;

            foreach ($ruleList as $rule) {
                if ($rule === 'required' && ($value === null || $value === '')) {
                    $errors[$field][] = 'Field is required';
                }

                if ($rule === 'email' && $value !== null && !filter_var($value, FILTER_VALIDATE_EMAIL)) {
                    $errors[$field][] = 'Invalid email';
                }

                if (str_starts_with($rule, 'min:') && $value !== null) {
                    $min = (int) substr($rule, 4);
                    if (strlen((string)$value) < $min) {
                        $errors[$field][] = "Minimum length is {$min}";
                    }
                }
            }
        }

        if (!empty($errors)) {
            json_error('Validation failed', 422, $errors);
        }
    }
}
