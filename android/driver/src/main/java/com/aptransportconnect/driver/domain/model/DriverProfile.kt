package com.aptransportconnect.driver.domain.model

data class DriverProfile(
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val vehicleNumber: String,
    val vehicleType: String,
    val rating: Float,
    val totalTrips: Int,
    val isOnline: Boolean,
    val profilePhotoUrl: String?
)
