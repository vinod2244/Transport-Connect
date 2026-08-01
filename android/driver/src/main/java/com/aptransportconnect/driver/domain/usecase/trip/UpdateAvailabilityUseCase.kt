package com.aptransportconnect.driver.domain.usecase.trip

import com.aptransportconnect.driver.domain.repository.TripRepository
import javax.inject.Inject

class UpdateAvailabilityUseCase @Inject constructor(
    private val tripRepository: TripRepository
) {
    suspend operator fun invoke(isOnline: Boolean): Result<Unit> = tripRepository.updateAvailability(isOnline)
}
