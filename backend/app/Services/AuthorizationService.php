<?php

namespace App\Services;

final class AuthorizationService
{
    public static function hasRole(string $role, array $allowedRoles): bool
    {
        return in_array($role, $allowedRoles, true);
    }

    public static function assertAnyRole(string $role, array $allowedRoles): void
    {
        if (!self::hasRole($role, $allowedRoles)) {
            throw new AuthException('Forbidden', 403, [
                'role' => 'The authenticated user does not have permission to access this resource.',
            ]);
        }
    }
}
