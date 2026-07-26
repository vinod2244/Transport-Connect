<?php

namespace App\Helpers;

use Firebase\JWT\JWT;
use Firebase\JWT\Key;
use Exception;

class JWTHelper
{
    private static string $secret;
    private static string $algorithm;

    public static function init(): void
    {
        self::$secret = config('jwt.secret');
        self::$algorithm = config('jwt.algorithm');
    }

    public static function encode(array $payload, int $expiry = null): string
    {
        self::init();
        
        $now = time();
        $expiry = $expiry ?? config('jwt.expiry');
        
        $payload = array_merge([
            'iss' => config('jwt.issuer'),
            'aud' => config('jwt.audience'),
            'iat' => $now,
            'exp' => $now + $expiry,
            'nbf' => $now,
            'jti' => bin2hex(random_bytes(16)),
        ], $payload);
        
        return JWT::encode($payload, self::$secret, self::$algorithm);
    }

    public static function decode(string $token): ?array
    {
        self::init();
        
        try {
            $decoded = JWT::decode($token, new Key(self::$secret, self::$algorithm));
            return (array) $decoded;
        } catch (Exception $e) {
            logger()->error('JWT decode error: ' . $e->getMessage());
            return null;
        }
    }

    public static function isValid(string $token): bool
    {
        return self::decode($token) !== null;
    }

    public static function getToken(string $header = 'Authorization'): ?string
    {
        $authHeader = $_SERVER[$header] ?? $_SERVER['HTTP_' . str_replace('-', '_', strtoupper($header))] ?? '';
        
        if (preg_match('/Bearer\s+(.*)$/i', $authHeader, $matches)) {
            return $matches[1];
        }
        
        return null;
    }
}
