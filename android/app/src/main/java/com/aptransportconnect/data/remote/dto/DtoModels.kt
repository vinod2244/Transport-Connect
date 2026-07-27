package com.aptransportconnect.data.remote.dto

data class LoginRequest(val mobile: String, val password: String)
data class OtpRequest(val mobile: String, val otp: String)
data class TokenResponse(val token: String, val refreshToken: String, val role: String)

data class VehicleDto(
    val id: String,
    val name: String,
    val type: String,
    val capacityTons: Double,
    val imageUrl: String,
)

data class BookingDto(
    val id: String,
    val vehicleId: String,
    val pickup: String,
    val drop: String,
    val status: String,
    val amount: Double,
    val etaMinutes: Int,
)

data class TrackingDto(
    val bookingId: String,
    val lat: Double,
    val lng: Double,
    val driverName: String,
)

data class PaymentRequest(val bookingId: String, val amount: Double)
data class PaymentDto(val bookingId: String, val state: String, val transactionId: String?)

data class NotificationDto(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long,
)

data class ChatDto(
    val id: String,
    val threadId: String,
    val text: String,
    val sender: String,
    val timestamp: Long,
)

data class ProfileDto(
    val name: String,
    val mobile: String,
    val avatarUrl: String,
    val email: String,
)
