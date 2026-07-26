<?php

namespace App\Core;

use App\Services\AuthException;
use App\Services\AuthService;
use App\Services\AuthorizationService;
use ReflectionMethod;
use Throwable;

final class ApiApplication
{
    public function handle(Request $request): array
    {
        try {
            [$route, $params] = $this->matchRoute($request);

            if (!$route) {
                return [404, error_response(404, 'Route not found')];
            }

            foreach ($params as $key => $value) {
                $request->setAttribute($key, $value);
            }

            if (!empty($route['auth'])) {
                $authContext = (new AuthService())->authenticateRequest($request);
                $request->setAttribute('auth', $authContext);

                if (!empty($route['roles'])) {
                    AuthorizationService::assertAnyRole($authContext['user']['role'], $route['roles']);
                }
            }

            [$controllerName, $method] = explode('@', $route['action'], 2);
            $controllerClass = 'App\\Controllers\\' . $controllerName . 'Controller';

            if (!class_exists($controllerClass)) {
                return [501, error_response(501, 'Endpoint handler is not implemented')];
            }

            $controller = new $controllerClass();

            if (!method_exists($controller, $method)) {
                return [501, error_response(501, 'Endpoint handler is not implemented')];
            }

            $reflection = new ReflectionMethod($controller, $method);
            $result = $reflection->getNumberOfParameters() > 0
                ? $controller->{$method}($request)
                : $controller->{$method}();
            $status = (int) ($result['status'] ?? 200);

            return [$status, $result];
        } catch (AuthException $exception) {
            return [
                $exception->status(),
                error_response($exception->status(), $exception->getMessage(), array_filter($exception->errors(), static fn ($value) => $value !== null)),
            ];
        } catch (Throwable $exception) {
            logger()->error('Unhandled API exception: ' . $exception->getMessage(), [
                'trace' => $exception->getTraceAsString(),
            ]);

            return [500, error_response(500, 'Internal server error')];
        }
    }

    private function matchRoute(Request $request): array
    {
        $routeKey = $request->method() . ':' . $request->path();

        foreach ($this->routes() as $route) {
            $pattern = preg_replace('/\{([a-zA-Z_][a-zA-Z0-9_]*)\}/', '(?P<$1>[^/]+)', $route['path']);
            $regex = '#^' . $route['method'] . ':' . $pattern . '$#';

            if (!preg_match($regex, $routeKey, $matches)) {
                continue;
            }

            $params = [];
            foreach ($matches as $key => $value) {
                if (is_string($key)) {
                    $params[$key] = $value;
                }
            }

            return [$route, $params];
        }

        return [null, []];
    }

    private function routes(): array
    {
        return [
            ['method' => 'GET', 'path' => '/health', 'action' => 'Health@check'],

            ['method' => 'POST', 'path' => '/auth/register', 'action' => 'Auth@register'],
            ['method' => 'POST', 'path' => '/auth/login', 'action' => 'Auth@login'],
            ['method' => 'POST', 'path' => '/auth/send-otp', 'action' => 'Auth@sendOtp'],
            ['method' => 'POST', 'path' => '/auth/verify-otp', 'action' => 'Auth@verifyOtp'],
            ['method' => 'POST', 'path' => '/auth/refresh', 'action' => 'Auth@refreshToken'],
            ['method' => 'POST', 'path' => '/auth/logout', 'action' => 'Auth@logout', 'auth' => true],
            ['method' => 'POST', 'path' => '/auth/logout-all', 'action' => 'Auth@logoutAll', 'auth' => true],
            ['method' => 'GET', 'path' => '/auth/sessions', 'action' => 'Auth@sessions', 'auth' => true],
            ['method' => 'POST', 'path' => '/auth/forgot-password', 'action' => 'Auth@forgotPassword'],
            ['method' => 'POST', 'path' => '/auth/verify-password-reset', 'action' => 'Auth@verifyPasswordReset'],
            ['method' => 'POST', 'path' => '/auth/reset-password', 'action' => 'Auth@resetPassword'],
            ['method' => 'POST', 'path' => '/auth/change-password', 'action' => 'Auth@changePassword', 'auth' => true],
            ['method' => 'POST', 'path' => '/auth/google', 'action' => 'Auth@googleLogin'],

            ['method' => 'GET', 'path' => '/user/profile', 'action' => 'User@getProfile', 'auth' => true, 'roles' => ['customer', 'admin']],
            ['method' => 'PUT', 'path' => '/user/profile', 'action' => 'User@updateProfile', 'auth' => true, 'roles' => ['customer', 'admin']],
            ['method' => 'GET', 'path' => '/driver/dashboard', 'action' => 'Driver@getDashboard', 'auth' => true, 'roles' => ['driver', 'admin']],
            ['method' => 'GET', 'path' => '/driver/profile', 'action' => 'Driver@getProfile', 'auth' => true, 'roles' => ['driver', 'admin']],
            ['method' => 'GET', 'path' => '/owner/dashboard', 'action' => 'Owner@getDashboard', 'auth' => true, 'roles' => ['owner', 'admin']],
            ['method' => 'GET', 'path' => '/owner/profile', 'action' => 'Owner@getProfile', 'auth' => true, 'roles' => ['owner', 'admin']],
            ['method' => 'GET', 'path' => '/admin/dashboard', 'action' => 'Admin@getDashboard', 'auth' => true, 'roles' => ['admin']],
            ['method' => 'GET', 'path' => '/admin/users', 'action' => 'Admin@getUsers', 'auth' => true, 'roles' => ['admin']],
        ];
    }
}
