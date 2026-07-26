<?php

namespace App\Core;

final class Request
{
    private array $attributes = [];

    public function __construct(
        private readonly string $method,
        private readonly string $path,
        private readonly array $headers = [],
        private readonly array $body = [],
        private readonly array $query = []
    ) {
    }

    public static function capture(): self
    {
        $path = parse_url($_SERVER['REQUEST_URI'] ?? '/', PHP_URL_PATH) ?? '/';
        $headers = function_exists('getallheaders') ? getallheaders() : [];
        $rawBody = file_get_contents('php://input') ?: '';
        $body = json_decode($rawBody, true);

        return new self(
            strtoupper($_SERVER['REQUEST_METHOD'] ?? 'GET'),
            '/' . trim(str_replace('/api', '', $path), '/'),
            is_array($headers) ? $headers : [],
            is_array($body) ? $body : [],
            $_GET
        );
    }

    public static function fromArray(
        string $method,
        string $path,
        array $body = [],
        array $headers = [],
        array $query = []
    ): self {
        return new self(strtoupper($method), $path, $headers, $body, $query);
    }

    public function method(): string
    {
        return $this->method;
    }

    public function path(): string
    {
        return $this->path;
    }

    public function body(): array
    {
        return $this->body;
    }

    public function all(): array
    {
        return array_merge($this->query, $this->body);
    }

    public function input(string $key, mixed $default = null): mixed
    {
        return $this->body[$key] ?? $this->query[$key] ?? $default;
    }

    public function query(string $key, mixed $default = null): mixed
    {
        return $this->query[$key] ?? $default;
    }

    public function header(string $key, mixed $default = null): mixed
    {
        foreach ($this->headers as $header => $value) {
            if (strcasecmp($header, $key) === 0) {
                return $value;
            }
        }

        return $default;
    }

    public function headers(): array
    {
        return $this->headers;
    }

    public function bearerToken(): ?string
    {
        $header = $this->header('Authorization');

        if (is_string($header) && preg_match('/Bearer\s+(.+)/i', $header, $matches)) {
            return trim($matches[1]);
        }

        return null;
    }

    public function ip(): string
    {
        return (string) ($this->header('X-Forwarded-For')
            ?? $_SERVER['REMOTE_ADDR']
            ?? '127.0.0.1');
    }

    public function userAgent(): string
    {
        return (string) ($this->header('User-Agent', 'unknown'));
    }

    public function setAttribute(string $key, mixed $value): void
    {
        $this->attributes[$key] = $value;
    }

    public function attribute(string $key, mixed $default = null): mixed
    {
        return $this->attributes[$key] ?? $default;
    }
}
