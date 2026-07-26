<?php

declare(strict_types=1);

namespace App\Core;

final class Router
{
    private array $routes = [];

    public function get(string $path, callable|array $handler, array $middleware = []): void
    {
        $this->map('GET', $path, $handler, $middleware);
    }

    public function post(string $path, callable|array $handler, array $middleware = []): void
    {
        $this->map('POST', $path, $handler, $middleware);
    }

    public function put(string $path, callable|array $handler, array $middleware = []): void
    {
        $this->map('PUT', $path, $handler, $middleware);
    }

    public function delete(string $path, callable|array $handler, array $middleware = []): void
    {
        $this->map('DELETE', $path, $handler, $middleware);
    }

    private function map(string $method, string $path, callable|array $handler, array $middleware): void
    {
        $this->routes[] = compact('method', 'path', 'handler', 'middleware');
    }

    public function dispatch(Request $request): mixed
    {
        foreach ($this->routes as $route) {
            if ($route['method'] !== $request->method()) continue;

            $params = $this->match($route['path'], $request->path());
            if ($params === null) continue;

            foreach ($params as $k => $v) {
                $request->setAttribute($k, $v);
            }

            $runner = array_reduce(
                array_reverse($route['middleware']),
                fn($next, $mw) => fn($req) => (new $mw())->handle($req, $next),
                fn($req) => is_array($route['handler'])
                    ? (new $route['handler'][0])->{$route['handler'][1]}($req)
                    : ($route['handler'])($req)
            );

            return $runner($request);
        }

        json_error('Not Found', 404);
    }

    private function match(string $routePath, string $requestPath): ?array
    {
        $pattern = preg_replace('#\\{([a-zA-Z_][a-zA-Z0-9_]*)\\}#', '(?P<$1>[^/]+)', $routePath);
        $pattern = '#^' . $pattern . '$#';

        if (!preg_match($pattern, $requestPath, $matches)) {
            return null;
        }

        $params = [];
        foreach ($matches as $k => $v) {
            if (is_string($k)) $params[$k] = $v;
        }
        return $params;
    }
}
