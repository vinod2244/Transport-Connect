package com.aptransportconnect.driver.domain.usecase.trip

import com.aptransportconnect.driver.domain.repository.TripRepository
import javax.inject.Inject

class RejectTripUseCase @Inject constructor(
    private val tripRepository: TripRepository
) {
    suspend operator fun invoke(tripId: String): Result<Unit> = tripRepository.rejectTrip(tripId)
}
