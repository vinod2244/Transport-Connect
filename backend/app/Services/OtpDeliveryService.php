<?php

namespace App\Services;

final class OtpDeliveryService
{
    public function send(string $channel, string $destination, string $otp, string $purpose): array
    {
        logger()->info('OTP generated', [
            'channel' => $channel,
            'destination' => $destination,
            'purpose' => $purpose,
        ]);

        return [
            'channel' => $channel,
            'destination' => $this->maskDestination($channel, $destination),
            'provider' => 'log',
        ];
    }

    private function maskDestination(string $channel, string $destination): string
    {
        if ($channel === 'email') {
            [$name, $domain] = array_pad(explode('@', $destination, 2), 2, '');
            $visible = substr($name, 0, 2);
            return $visible . str_repeat('*', max(strlen($name) - 2, 1)) . '@' . $domain;
        }

        $length = strlen($destination);

        if ($length <= 4) {
            return str_repeat('*', $length);
        }

        return str_repeat('*', $length - 4) . substr($destination, -4);
    }
}
