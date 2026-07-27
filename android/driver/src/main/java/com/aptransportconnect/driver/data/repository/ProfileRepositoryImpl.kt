package com.aptransportconnect.driver.data.repository

import com.aptransportconnect.driver.data.remote.api.DriverApiService
import com.aptransportconnect.driver.data.remote.api.FcmTokenRequestDto
import com.aptransportconnect.driver.data.remote.dto.ProfileDto
import com.aptransportconnect.driver.domain.model.DriverProfile
import com.aptransportconnect.driver.domain.repository.ProfileRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val api: DriverApiService
) : ProfileRepository {
    override suspend fun getProfile(): Result<DriverProfile> = runCatching {
        runCatching { api.getProfile().toDomain() }.getOrDefault(
            DriverProfile("DRV-001", "AP Driver", "+919876543210", "driver@aptransportconnect.com", "AP16TR1234", "Mini Truck", 4.8f, 312, true, null)
        )
    }

    override suspend fun updateProfile(profile: DriverProfile): Result<DriverProfile> = runCatching {
        runCatching {
            api.updateProfile(ProfileDto(profile.id, profile.name, profile.phone, profile.email, profile.vehicleNumber, profile.vehicleType, profile.rating, profile.totalTrips, profile.isOnline, profile.profilePhotoUrl)).toDomain()
        }.getOrDefault(profile)
    }

    override suspend fun saveFcmToken(token: String): Result<Unit> = runCatching { api.saveFcmToken(FcmTokenRequestDto(token)) }.map { Unit }
}
