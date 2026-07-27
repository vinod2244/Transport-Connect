package com.aptransportconnect.driver.presentation.trip.requests

import com.aptransportconnect.driver.MainDispatcherRule
import com.aptransportconnect.driver.domain.model.Trip
import com.aptransportconnect.driver.domain.model.TripStatus
import com.aptransportconnect.driver.domain.repository.TripRepository
import com.aptransportconnect.driver.domain.usecase.trip.AcceptTripUseCase
import com.aptransportconnect.driver.domain.usecase.trip.GetTripRequestsUseCase
import com.aptransportconnect.driver.domain.usecase.trip.RejectTripUseCase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TripRequestsViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun acceptTrip_refreshesState() = runTest {
        val repository = FakeTripRepository()
        val viewModel = TripRequestsViewModel(GetTripRequestsUseCase(repository), AcceptTripUseCase(repository), RejectTripUseCase(repository))
        advanceUntilIdle()
        viewModel.acceptTrip("1")
        advanceUntilIdle()
        val state = viewModel.uiState.value as TripRequestsUiState.Success
        assertThat(state.requests).isEmpty()
    }

    @Test
    fun rejectTrip_refreshesState() = runTest {
        val repository = FakeTripRepository()
        val viewModel = TripRequestsViewModel(GetTripRequestsUseCase(repository), AcceptTripUseCase(repository), RejectTripUseCase(repository))
        advanceUntilIdle()
        viewModel.rejectTrip("1")
        advanceUntilIdle()
        val state = viewModel.uiState.value as TripRequestsUiState.Success
        assertThat(state.requests).isEmpty()
    }

    @Test
    fun expiredTrips_areRemovedFromState() = runTest {
        val repository = FakeTripRepository(requestedAt = System.currentTimeMillis() - 61_000)
        val viewModel = TripRequestsViewModel(GetTripRequestsUseCase(repository), AcceptTripUseCase(repository), RejectTripUseCase(repository))
        advanceUntilIdle()
        val state = viewModel.uiState.value as TripRequestsUiState.Success
        assertThat(state.requests).isEmpty()
    }
}

private class FakeTripRepository(private val requestedAt: Long = System.currentTimeMillis()) : TripRepository {
    private var trips = mutableListOf(sampleTrip(requestedAt))
    override fun observeTripRequests(): Flow<List<Trip>> = flowOf(trips)
    override fun observeCurrentTrip(): Flow<Trip?> = flowOf(null)
    override fun observeTripHistory(): Flow<List<Trip>> = flowOf(trips)
    override suspend fun refreshTripRequests(): Result<List<Trip>> = Result.success(trips)
    override suspend fun refreshCurrentTrip(): Result<Trip?> = Result.success(null)
    override suspend fun refreshTripHistory(date: String?): Result<List<Trip>> = Result.success(trips)
    override suspend fun getTripById(tripId: String): Result<Trip?> = Result.success(trips.firstOrNull { it.id == tripId })
    override suspend fun acceptTrip(tripId: String): Result<Trip> { trips.removeAll { it.id == tripId }; return Result.success(sampleTrip(requestedAt).copy(status = TripStatus.ACCEPTED)) }
    override suspend fun rejectTrip(tripId: String): Result<Unit> { trips.removeAll { it.id == tripId }; return Result.success(Unit) }
    override suspend fun updateTripStatus(tripId: String, status: TripStatus): Result<Trip> = Result.success(sampleTrip(requestedAt).copy(status = status))
    override suspend fun updateAvailability(isOnline: Boolean): Result<Unit> = Result.success(Unit)
}

private fun sampleTrip(requestedAt: Long) = Trip(
    id = "1", customerId = "c1", customerName = "Customer", customerPhone = "99999",
    pickupAddress = "A", dropAddress = "B", pickupLat = 0.0, pickupLng = 0.0, dropLat = 1.0, dropLng = 1.0,
    estimatedFare = 100.0, distance = 10.0, status = TripStatus.PENDING, vehicleType = "Mini", requestedAt = requestedAt
)
