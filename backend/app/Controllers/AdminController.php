<?php

namespace App\Controllers;

use App\Core\Request;

class AdminController
{
    public function getDashboard(Request $request): array
    {
        $auth = $request->attribute('auth');

        return response(200, [
            'admin' => $auth['user'],
        ], 'Admin dashboard fetched successfully');
    }

    public function getUsers(Request $request): array
    {
        $auth = $request->attribute('auth');

        return response(200, [
            'admin' => $auth['user'],
            'users' => [],
        ], 'User list fetched successfully');
    }
}
