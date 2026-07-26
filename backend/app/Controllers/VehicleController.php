<?php

namespace App\Controllers;

class VehicleController
{
    public function search(): array
    {
        // TODO: Implement vehicle search
        return response(200, ['vehicles' => []]);
    }

    public function getDetail(): array
    {
        // TODO: Implement get vehicle detail
        return response(200, []);
    }

    public function getNearby(): array
    {
        // TODO: Implement get nearby vehicles
        return response(200, ['vehicles' => []]);
    }

    public function getTypes(): array
    {
        return response(200, [
            'types' => [
                'mini_truck' => 'Mini Truck',
                'pickup' => 'Pickup',
                'tata_ace' => 'Tata Ace',
                'container' => 'Container',
                'trailer' => 'Trailer',
                'tipper' => 'Tipper',
                'lorry' => 'Lorry',
                'flatbed' => 'Flatbed',
                'refrigerated' => 'Refrigerated Truck',
                'tanker' => 'Tanker',
                'heavy_truck' => 'Heavy Truck',
                'lcv' => 'Light Commercial Vehicle',
            ]
        ]);
    }
}
