package com.aptransportconnect.driver.presentation.trip.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aptransportconnect.driver.domain.model.Trip
import com.aptransportconnect.driver.domain.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface TripDetailsUiState {
    data object Loading : TripDetailsUiState
    data class Success(val trip: Trip) : TripDetailsUiState
    data class Error(val message: String) : TripDetailsUiState
}

@HiltViewModel
class TripDetailsViewModel @Inject constructor(
    private val tripRepository: TripRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<TripDetailsUiState>(TripDetailsUiState.Loading)
    val uiState: StateFlow<TripDetailsUiState> = _uiState.asStateFlow()

    fun loadTrip(tripId: String) {
        viewModelScope.launch {
            _uiState.value = TripDetailsUiState.Loading
            tripRepository.getTripById(tripId)
                .onSuccess { trip ->
                    if (trip != null) _uiState.value = TripDetailsUiState.Success(trip)
                    else _uiState.value = TripDetailsUiState.Error("Trip not found")
                }
                .onFailure { _uiState.value = TripDetailsUiState.Error(it.message ?: "Unable to load trip") }
        }
    }
}
