<?php

namespace App\Controllers;

use App\Core\Request;

class DriverController
{
    public function getDashboard(Request $request): array
    {
        $auth = $request->attribute('auth');
        return response(200, [
            'driver' => $auth['user'],
        ], 'Driver dashboard fetched successfully');
    }

    public function getTrips(): array
    {
        // TODO: Implement get trips
        return response(200, ['trips' => []]);
    }

    public function getTripDetail(): array
    {
        // TODO: Implement get trip detail
        return response(200, []);
    }

    public function acceptTrip(): array
    {
        // TODO: Implement accept trip
        return response(200, ['message' => 'Trip accepted successfully']);
    }

    public function rejectTrip(): array
    {
        // TODO: Implement reject trip
        return response(200, ['message' => 'Trip rejected successfully']);
    }

    public function startTrip(): array
    {
        // TODO: Implement start trip
        return response(200, ['message' => 'Trip started successfully']);
    }

    public function completeTrip(): array
    {
        // TODO: Implement complete trip
        return response(200, ['message' => 'Trip completed successfully']);
    }

    public function updateLocation(): array
    {
        // TODO: Implement update location
        return response(200, ['message' => 'Location updated successfully']);
    }

    public function getEarnings(): array
    {
        // TODO: Implement get earnings
        return response(200, []);
    }

    public function getWallet(): array
    {
        // TODO: Implement get wallet
        return response(200, []);
    }

    public function requestWithdrawal(): array
    {
        // TODO: Implement request withdrawal
        return response(200, ['message' => 'Withdrawal requested successfully']);
    }

    public function getDocuments(): array
    {
        // TODO: Implement get documents
        return response(200, ['documents' => []]);
    }

    public function uploadDocument(): array
    {
        // TODO: Implement upload document
        return response(201, ['message' => 'Document uploaded successfully']);
    }

    public function getProfile(Request $request): array
    {
        $auth = $request->attribute('auth');
        return response(200, [
            'driver' => $auth['user'],
        ], 'Driver profile fetched successfully');
    }

    public function updateProfile(): array
    {
        // TODO: Implement update profile
        return response(200, ['message' => 'Profile updated successfully']);
    }
}
