<?php

namespace App\Controllers;

class AuthController
{
    public function register(): array
    {
        // TODO: Implement user registration
        return response(201, ['message' => 'User registered successfully']);
    }

    public function login(): array
    {
        // TODO: Implement user login
        return response(200, ['token' => 'jwt_token_here']);
    }

    public function verifyOtp(): array
    {
        // TODO: Implement OTP verification
        return response(200, ['verified' => true]);
    }

    public function sendOtp(): array
    {
        // TODO: Implement OTP sending
        return response(200, ['message' => 'OTP sent successfully']);
    }

    public function refreshToken(): array
    {
        // TODO: Implement token refresh
        return response(200, ['token' => 'new_jwt_token_here']);
    }

    public function logout(): array
    {
        // TODO: Implement logout
        return response(200, ['message' => 'Logged out successfully']);
    }

    public function forgotPassword(): array
    {
        // TODO: Implement forgot password
        return response(200, ['message' => 'Password reset link sent']);
    }

    public function resetPassword(): array
    {
        // TODO: Implement password reset
        return response(200, ['message' => 'Password reset successfully']);
    }

    public function googleLogin(): array
    {
        // TODO: Implement Google login
        return response(200, ['token' => 'jwt_token_here']);
    }
}
