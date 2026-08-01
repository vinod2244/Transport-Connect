package com.aptransportconnect.driver.domain.usecase.location

import com.aptransportconnect.driver.domain.repository.LocationRepository
import javax.inject.Inject

class UpdateLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(lat: Double, lng: Double, heading: Float): Result<Unit> =
        locationRepository.updateLocation(lat, lng, heading)
}
