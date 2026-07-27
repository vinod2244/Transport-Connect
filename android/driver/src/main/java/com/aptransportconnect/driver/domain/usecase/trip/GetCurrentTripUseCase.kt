package com.aptransportconnect.driver.domain.usecase.trip

import com.aptransportconnect.driver.domain.model.Trip
import com.aptransportconnect.driver.domain.repository.TripRepository
import javax.inject.Inject

class GetCurrentTripUseCase @Inject constructor(
    private val tripRepository: TripRepository
) {
    suspend operator fun invoke(): Result<Trip?> = tripRepository.refreshCurrentTrip()
}
