package com.aptransportconnect.driver.domain.repository

import com.aptransportconnect.driver.domain.model.DriverProfile

interface ProfileRepository {
    suspend fun getProfile(): Result<DriverProfile>
    suspend fun updateProfile(profile: DriverProfile): Result<DriverProfile>
    suspend fun saveFcmToken(token: String): Result<Unit>
}
