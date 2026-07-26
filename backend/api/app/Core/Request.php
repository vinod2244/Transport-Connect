<?php

declare(strict_types=1);

namespace App\Core;

final class Request
{
    private array $attributes = [];

    public static function capture(): self
    {
        return new self();
    }

    public function method(): string
    {
        $method = strtoupper($_SERVER['REQUEST_METHOD'] ?? 'GET');

        if ($method === 'POST') {
            $override = $_SERVER['HTTP_X_HTTP_METHOD_OVERRIDE'] ?? null;
            if (!$override) {
                $body = $this->json();
                $override = $body['_method'] ?? null;
            }
            if ($override) $method = strtoupper((string)$override);
        }

        return $method;
    }

    public function path(): string
    {
        $uri = $_SERVER['REQUEST_URI'] ?? '/';
        return '/' . trim(parse_url($uri, PHP_URL_PATH) ?? '/', '/');
    }

    public function json(): array
    {
        $raw = file_get_contents('php://input') ?: '{}';
        $data = json_decode($raw, true);
        return is_array($data) ? $data : [];
    }

    public function header(string $key): ?string
    {
        $headers = getallheaders();
        return $headers[$key] ?? $headers[strtolower($key)] ?? null;
    }

    public function query(string $key, mixed $default = null): mixed
    {
        return $_GET[$key] ?? $default;
    }

    public function setAttribute(string $key, mixed $value): void
    {
        $this->attributes[$key] = $value;
    }

    public function attribute(string $key): mixed
    {
        return $this->attributes[$key] ?? null;
    }
}
