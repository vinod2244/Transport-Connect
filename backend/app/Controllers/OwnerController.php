<?php

namespace App\Controllers;

use App\Core\Request;

class OwnerController
{
    public function getDashboard(Request $request): array
    {
        $auth = $request->attribute('auth');

        return response(200, [
            'owner' => $auth['user'],
        ], 'Owner dashboard fetched successfully');
    }

    public function getProfile(Request $request): array
    {
        $auth = $request->attribute('auth');

        return response(200, [
            'owner' => $auth['user'],
        ], 'Owner profile fetched successfully');
    }
}
