package com.aptransportconnect.driver.data.remote.dto

import com.aptransportconnect.driver.domain.model.DriverProfile

data class ProfileDto(
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
) {
    fun toDomain() = DriverProfile(id, name, phone, email, vehicleNumber, vehicleType, rating, totalTrips, isOnline, profilePhotoUrl)
}
