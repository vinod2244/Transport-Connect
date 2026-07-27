package com.aptransportconnect.domain.model

data class UserSession(
    val token: String,
    val refreshToken: String,
    val role: String,
    val mobile: String,
)

data class Vehicle(
    val id: String,
    val name: String,
    val type: String,
    val capacityTons: Double,
    val imageUrl: String,
)

data class Booking(
    val id: String,
    val vehicleId: String,
    val pickup: String,
    val drop: String,
    val status: String,
    val amount: Double,
    val etaMinutes: Int,
)

data class TrackingUpdate(
    val bookingId: String,
    val lat: Double,
    val lng: Double,
    val driverName: String,
)

data class PaymentResult(
    val bookingId: String,
    val state: PaymentState,
    val transactionId: String?,
)

enum class PaymentState { INITIATED, SUCCESS, FAILURE }

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long,
)

data class ChatMessage(
    val id: String,
    val threadId: String,
    val text: String,
    val sender: String,
    val timestamp: Long,
)

data class Profile(
    val name: String,
    val mobile: String,
    val avatarUrl: String,
    val email: String,
)
