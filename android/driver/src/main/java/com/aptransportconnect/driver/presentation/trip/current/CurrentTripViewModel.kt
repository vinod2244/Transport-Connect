package com.aptransportconnect.driver.presentation.trip.current

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aptransportconnect.driver.domain.model.Trip
import com.aptransportconnect.driver.domain.model.TripStatus
import com.aptransportconnect.driver.domain.repository.TripRepository
import com.aptransportconnect.driver.domain.usecase.trip.GetCurrentTripUseCase
import com.aptransportconnect.driver.domain.usecase.trip.UpdateTripStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface CurrentTripUiState {
    data object Loading : CurrentTripUiState
    data class Success(val trip: Trip) : CurrentTripUiState
    data class Error(val message: String) : CurrentTripUiState
}

@HiltViewModel
class CurrentTripViewModel @Inject constructor(
    private val getCurrentTripUseCase: GetCurrentTripUseCase,
    private val updateTripStatusUseCase: UpdateTripStatusUseCase,
    private val tripRepository: TripRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<CurrentTripUiState>(CurrentTripUiState.Loading)
    val uiState: StateFlow<CurrentTripUiState> = _uiState.asStateFlow()

    init {
        refresh()
        viewModelScope.launch {
            tripRepository.observeCurrentTrip().collect { trip ->
                if (trip != null) _uiState.value = CurrentTripUiState.Success(trip)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = CurrentTripUiState.Loading
            getCurrentTripUseCase()
                .onSuccess { trip ->
                    if (trip != null) _uiState.value = CurrentTripUiState.Success(trip)
                    else _uiState.value = CurrentTripUiState.Error("No active trip")
                }
                .onFailure { _uiState.value = CurrentTripUiState.Error(it.message ?: "Unable to load trip") }
        }
    }

    fun advanceStatus() {
        val trip = (uiState.value as? CurrentTripUiState.Success)?.trip ?: return
        val nextStatus = when (trip.status) {
            TripStatus.ACCEPTED -> TripStatus.EN_ROUTE_PICKUP
            TripStatus.EN_ROUTE_PICKUP -> TripStatus.ARRIVED
            TripStatus.ARRIVED -> TripStatus.TRIP_STARTED
            TripStatus.TRIP_STARTED -> TripStatus.COMPLETED
            else -> trip.status
        }
        viewModelScope.launch {
            updateTripStatusUseCase(trip.id, nextStatus)
                .onSuccess { _uiState.value = CurrentTripUiState.Success(it) }
                .onFailure { _uiState.value = CurrentTripUiState.Error(it.message ?: "Status update failed") }
        }
    }

    fun actionLabel(status: TripStatus): String = when (status) {
        TripStatus.ACCEPTED -> "Navigate to Pickup"
        TripStatus.EN_ROUTE_PICKUP -> "Arrived at Pickup"
        TripStatus.ARRIVED -> "Start Trip"
        TripStatus.TRIP_STARTED -> "Complete Trip"
        else -> "Trip Updated"
    }
}
