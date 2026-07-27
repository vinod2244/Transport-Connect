package com.aptransportconnect.driver.data.repository

import com.aptransportconnect.driver.data.remote.api.DriverApiService
import com.aptransportconnect.driver.data.remote.api.LocationUpdateRequestDto
import com.aptransportconnect.driver.domain.repository.LocationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val api: DriverApiService
) : LocationRepository {
    override suspend fun updateLocation(lat: Double, lng: Double, heading: Float): Result<Unit> =
        runCatching { api.updateLocation(LocationUpdateRequestDto(lat, lng, heading)) }.map { Unit }
}
