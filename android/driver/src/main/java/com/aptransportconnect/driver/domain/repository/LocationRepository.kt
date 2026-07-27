package com.aptransportconnect.driver.domain.repository

interface LocationRepository {
    suspend fun updateLocation(lat: Double, lng: Double, heading: Float): Result<Unit>
}
