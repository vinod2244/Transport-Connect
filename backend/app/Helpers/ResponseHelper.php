<?php

namespace App\Helpers;

class ResponseHelper
{
    public static function success(array $data = [], string $message = 'Success', int $status = 200): array
    {
        return [
            'status' => $status,
            'message' => $message,
            'data' => $data,
            'timestamp' => date('Y-m-d H:i:s'),
        ];
    }

    public static function error(string $message = 'Error', int $status = 400, array $errors = []): array
    {
        return [
            'status' => $status,
            'message' => $message,
            'errors' => $errors,
            'timestamp' => date('Y-m-d H:i:s'),
        ];
    }

    public static function paginated(array $data, int $total, int $page, int $pageSize): array
    {
        return [
            'data' => $data,
            'pagination' => [
                'total' => $total,
                'current_page' => $page,
                'page_size' => $pageSize,
                'total_pages' => ceil($total / $pageSize),
            ],
        ];
    }

    public static function json(array $response, int $statusCode = 200): void
    {
        header('Content-Type: application/json');
        http_response_code($statusCode);
        echo json_encode($response);
    }
}
