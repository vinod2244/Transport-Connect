<?php

namespace App\Controllers;

class HealthController
{
    public function check(): array
    {
        return response(200, [
            'status' => 'healthy',
            'service' => 'AP Transport Connect API',
            'version' => '1.0.0',
            'timestamp' => date('Y-m-d H:i:s'),
            'database' => $this->checkDatabase(),
        ], 'Health check passed');
    }

    private function checkDatabase(): string
    {
        try {
            // TODO: Implement database connection check
            return 'connected';
        } catch (\Exception $e) {
            return 'disconnected';
        }
    }
}
