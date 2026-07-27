package com.aptransportconnect.driver.data.remote.dto

import com.aptransportconnect.driver.domain.model.Trip
import com.aptransportconnect.driver.domain.model.TripStatus

data class TripDto(
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
    val status: String,
    val vehicleType: String,
    val requestedAt: Long,
    val startedAt: Long? = null,
    val completedAt: Long? = null,
    val actualFare: Double? = null
) {
    fun toDomain() = Trip(
        id = id,
        customerId = customerId,
        customerName = customerName,
        customerPhone = customerPhone,
        pickupAddress = pickupAddress,
        dropAddress = dropAddress,
        pickupLat = pickupLat,
        pickupLng = pickupLng,
        dropLat = dropLat,
        dropLng = dropLng,
        estimatedFare = estimatedFare,
        distance = distance,
        status = runCatching { TripStatus.valueOf(status.uppercase()) }.getOrDefault(TripStatus.PENDING),
        vehicleType = vehicleType,
        requestedAt = requestedAt,
        startedAt = startedAt,
        completedAt = completedAt,
        actualFare = actualFare
    )
}
