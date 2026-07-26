<?php

namespace App\Controllers;

class UserController
{
    public function getProfile(): array
    {
        // TODO: Implement get profile
        return response(200, []);
    }

    public function updateProfile(): array
    {
        // TODO: Implement update profile
        return response(200, ['message' => 'Profile updated successfully']);
    }

    public function getBookings(): array
    {
        // TODO: Implement get bookings
        return response(200, ['bookings' => []]);
    }

    public function getBookingDetail(): array
    {
        // TODO: Implement get booking detail
        return response(200, []);
    }

    public function createBooking(): array
    {
        // TODO: Implement create booking
        return response(201, ['message' => 'Booking created successfully']);
    }

    public function cancelBooking(): array
    {
        // TODO: Implement cancel booking
        return response(200, ['message' => 'Booking cancelled successfully']);
    }

    public function getWallet(): array
    {
        // TODO: Implement get wallet
        return response(200, []);
    }

    public function addMoney(): array
    {
        // TODO: Implement add money to wallet
        return response(200, ['message' => 'Money added to wallet']);
    }

    public function getRatings(): array
    {
        // TODO: Implement get ratings
        return response(200, ['ratings' => []]);
    }

    public function createRating(): array
    {
        // TODO: Implement create rating
        return response(201, ['message' => 'Rating created successfully']);
    }
}
