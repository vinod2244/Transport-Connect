package com.aptransportconnect.driver.presentation.trip.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aptransportconnect.driver.domain.model.Trip
import com.aptransportconnect.driver.domain.usecase.trip.AcceptTripUseCase
import com.aptransportconnect.driver.domain.usecase.trip.GetTripRequestsUseCase
import com.aptransportconnect.driver.domain.usecase.trip.RejectTripUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class TimedTripRequest(val trip: Trip, val secondsRemaining: Int)

sealed interface TripRequestsUiState {
    data object Loading : TripRequestsUiState
    data class Success(val requests: List<TimedTripRequest>) : TripRequestsUiState
    data class Error(val message: String) : TripRequestsUiState
}

@HiltViewModel
class TripRequestsViewModel @Inject constructor(
    private val getTripRequestsUseCase: GetTripRequestsUseCase,
    private val acceptTripUseCase: AcceptTripUseCase,
    private val rejectTripUseCase: RejectTripUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<TripRequestsUiState>(TripRequestsUiState.Loading)
    val uiState: StateFlow<TripRequestsUiState> = _uiState.asStateFlow()
    private var tickerJob: Job? = null

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = TripRequestsUiState.Loading
            getTripRequestsUseCase()
                .onSuccess {
                    _uiState.value = TripRequestsUiState.Success(mapTimedRequests(it))
                    startTicker()
                }
                .onFailure { _uiState.value = TripRequestsUiState.Error(it.message ?: "Unable to load requests") }
        }
    }

    fun acceptTrip(tripId: String) {
        viewModelScope.launch {
            acceptTripUseCase(tripId)
                .onSuccess { refresh() }
                .onFailure { _uiState.value = TripRequestsUiState.Error(it.message ?: "Unable to accept trip") }
        }
    }

    fun rejectTrip(tripId: String) {
        viewModelScope.launch {
            rejectTripUseCase(tripId)
                .onSuccess { refresh() }
                .onFailure { _uiState.value = TripRequestsUiState.Error(it.message ?: "Unable to reject trip") }
        }
    }

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            while (isActive) {
                val current = _uiState.value
                if (current is TripRequestsUiState.Success) {
                    _uiState.value = TripRequestsUiState.Success(mapTimedRequests(current.requests.map { it.trip }))
                }
                delay(1000)
            }
        }
    }

    private fun mapTimedRequests(trips: List<Trip>): List<TimedTripRequest> {
        val now = System.currentTimeMillis()
        return trips.mapNotNull { trip ->
            val remaining = 60 - ((now - trip.requestedAt) / 1000L).toInt()
            if (remaining > 0) TimedTripRequest(trip, remaining) else null
        }
    }
}
