package com.aptransportconnect.driver.presentation.trip.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aptransportconnect.driver.domain.model.Trip
import com.aptransportconnect.driver.domain.model.TripStatus
import com.aptransportconnect.driver.domain.usecase.trip.GetTripHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class TripHistoryFilter { ALL, COMPLETED, CANCELLED }

sealed interface TripHistoryUiState {
    data object Loading : TripHistoryUiState
    data class Success(val trips: List<Trip>, val filter: TripHistoryFilter) : TripHistoryUiState
    data class Error(val message: String) : TripHistoryUiState
}

@HiltViewModel
class TripHistoryViewModel @Inject constructor(
    private val getTripHistoryUseCase: GetTripHistoryUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<TripHistoryUiState>(TripHistoryUiState.Loading)
    val uiState: StateFlow<TripHistoryUiState> = _uiState.asStateFlow()
    private var allTrips: List<Trip> = emptyList()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = TripHistoryUiState.Loading
            getTripHistoryUseCase()
                .onSuccess {
                    allTrips = it
                    _uiState.value = TripHistoryUiState.Success(it, TripHistoryFilter.ALL)
                }
                .onFailure { _uiState.value = TripHistoryUiState.Error(it.message ?: "Unable to load history") }
        }
    }

    fun setFilter(filter: TripHistoryFilter) {
        val filtered = when (filter) {
            TripHistoryFilter.ALL -> allTrips
            TripHistoryFilter.COMPLETED -> allTrips.filter { it.status == TripStatus.COMPLETED }
            TripHistoryFilter.CANCELLED -> allTrips.filter { it.status == TripStatus.CANCELLED || it.status == TripStatus.REJECTED }
        }
        _uiState.value = TripHistoryUiState.Success(filtered, filter)
    }
}
