package com.aptransportconnect.driver.domain.repository

import com.aptransportconnect.driver.domain.model.Trip
import com.aptransportconnect.driver.domain.model.TripStatus
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    fun observeTripRequests(): Flow<List<Trip>>
    fun observeCurrentTrip(): Flow<Trip?>
    fun observeTripHistory(): Flow<List<Trip>>
    suspend fun refreshTripRequests(): Result<List<Trip>>
    suspend fun refreshCurrentTrip(): Result<Trip?>
    suspend fun refreshTripHistory(date: String? = null): Result<List<Trip>>
    suspend fun getTripById(tripId: String): Result<Trip?>
    suspend fun acceptTrip(tripId: String): Result<Trip>
    suspend fun rejectTrip(tripId: String): Result<Unit>
    suspend fun updateTripStatus(tripId: String, status: TripStatus): Result<Trip>
    suspend fun updateAvailability(isOnline: Boolean): Result<Unit>
}
