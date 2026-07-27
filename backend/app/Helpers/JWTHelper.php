<?php

namespace App\Helpers;

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

        $header = [
            'alg' => self::$algorithm,
            'typ' => 'JWT',
        ];

        $segments = [
            self::base64UrlEncode(json_encode($header, JSON_THROW_ON_ERROR)),
            self::base64UrlEncode(json_encode($payload, JSON_THROW_ON_ERROR)),
        ];

        $signature = hash_hmac('sha256', implode('.', $segments), self::$secret, true);
        $segments[] = self::base64UrlEncode($signature);

        return implode('.', $segments);
    }

    public static function decode(string $token): ?array
    {
        self::init();
        
        try {
            $parts = explode('.', $token);

            if (count($parts) !== 3) {
                throw new Exception('Token structure is invalid');
            }

            [$encodedHeader, $encodedPayload, $encodedSignature] = $parts;
            $header = json_decode(self::base64UrlDecode($encodedHeader), true, 512, JSON_THROW_ON_ERROR);
            $payload = json_decode(self::base64UrlDecode($encodedPayload), true, 512, JSON_THROW_ON_ERROR);

            if (($header['alg'] ?? null) !== self::$algorithm) {
                throw new Exception('Token algorithm is invalid');
            }

            $expectedSignature = self::base64UrlEncode(
                hash_hmac('sha256', $encodedHeader . '.' . $encodedPayload, self::$secret, true)
            );

            if (!hash_equals($expectedSignature, $encodedSignature)) {
                throw new Exception('Token signature is invalid');
            }

            $now = time();

            if (($payload['nbf'] ?? $now) > $now) {
                throw new Exception('Token is not active yet');
            }

            if (($payload['exp'] ?? 0) < $now) {
                throw new Exception('Token has expired');
            }

            return $payload;
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

    private static function base64UrlEncode(string $data): string
    {
        return rtrim(strtr(base64_encode($data), '+/', '-_'), '=');
    }

    private static function base64UrlDecode(string $data): string
    {
        $padding = strlen($data) % 4;

        if ($padding !== 0) {
            $data .= str_repeat('=', 4 - $padding);
        }

        return base64_decode(strtr($data, '-_', '+/'), true) ?: '';
    }
}
