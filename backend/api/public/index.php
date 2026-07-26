<?php

declare(strict_types=1);

use App\Core\Router;
use App\Core\Request;
use App\Middleware\ErrorMiddleware;
use Dotenv\Dotenv;

require __DIR__ . '/../vendor/autoload.php';

$dotenv = Dotenv::createImmutable(__DIR__ . '/../');
$dotenv->safeLoad();

require __DIR__ . '/../app/Helpers/response.php';
require __DIR__ . '/../app/Helpers/jwt.php';

$router = new Router();
require __DIR__ . '/../app/Routes/api_v1.php';

$request = Request::capture();

try {
    (new ErrorMiddleware())->handle($request, fn($req) => $router->dispatch($req));
} catch (Throwable $e) {
    json_error('Internal server error', 500);
}
