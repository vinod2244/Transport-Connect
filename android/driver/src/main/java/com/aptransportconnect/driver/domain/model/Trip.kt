package com.aptransportconnect.driver.domain.model

data class Trip(
    val id: String,
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val pickupAddress: String,
    val dropAddress: String,
    val pickupLat: Double,
    val pickupLng: Double,
    val dropLat: Double,
    val dropLng: Double,
    val estimatedFare: Double,
    val distance: Double,
    val status: TripStatus,
    val vehicleType: String,
    val requestedAt: Long,
    val startedAt: Long? = null,
    val completedAt: Long? = null,
    val actualFare: Double? = null
)

enum class TripStatus { PENDING, ACCEPTED, EN_ROUTE_PICKUP, ARRIVED, TRIP_STARTED, COMPLETED, CANCELLED, REJECTED }
