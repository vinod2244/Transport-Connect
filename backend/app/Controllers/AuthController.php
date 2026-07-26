<?php

namespace App\Controllers;

use App\Core\Request;
use App\Services\AuthService;

class AuthController
{
    public function register(Request $request): array
    {
        return $this->service()->register($request->body());
    }

    public function login(Request $request): array
    {
        return $this->service()->loginWithPassword($request->body(), $request);
    }

    public function verifyOtp(Request $request): array
    {
        return $this->service()->verifyOtp($request->body(), $request);
    }

    public function sendOtp(Request $request): array
    {
        return $this->service()->sendOtp($request->body());
    }

    public function refreshToken(Request $request): array
    {
        return $this->service()->refreshTokens($request->body(), $request);
    }

    public function logout(Request $request): array
    {
        return $this->service()->logout($request->body(), $request->attribute('auth'));
    }

    public function logoutAll(Request $request): array
    {
        $auth = $request->attribute('auth');
        return $this->service()->logoutAllSessions((int) $auth['user']['id']);
    }

    public function sessions(Request $request): array
    {
        $auth = $request->attribute('auth');
        return $this->service()->listSessions((int) $auth['user']['id']);
    }

    public function forgotPassword(Request $request): array
    {
        return $this->service()->forgotPassword($request->body());
    }

    public function verifyPasswordReset(Request $request): array
    {
        return $this->service()->verifyPasswordReset($request->body());
    }

    public function resetPassword(Request $request): array
    {
        return $this->service()->resetPassword($request->body());
    }

    public function changePassword(Request $request): array
    {
        $auth = $request->attribute('auth');
        return $this->service()->changePassword((int) $auth['user']['id'], $request->body());
    }

    public function googleLogin(Request $request): array
    {
        return error_response(501, 'Google login is not implemented yet');
    }

    private function service(): AuthService
    {
        return new AuthService();
    }
}
