package com.aptransportconnect.driver.presentation.navigation_map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aptransportconnect.driver.domain.model.Trip
import com.aptransportconnect.driver.domain.usecase.trip.GetCurrentTripUseCase
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface NavigationUiState {
    data object Loading : NavigationUiState
    data class Success(val trip: Trip, val route: List<LatLng>) : NavigationUiState
    data class Error(val message: String) : NavigationUiState
}

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val getCurrentTripUseCase: GetCurrentTripUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<NavigationUiState>(NavigationUiState.Loading)
    val uiState: StateFlow<NavigationUiState> = _uiState.asStateFlow()

    init { loadRoute() }

    fun loadRoute() {
        viewModelScope.launch {
            _uiState.value = NavigationUiState.Loading
            getCurrentTripUseCase()
                .onSuccess { trip ->
                    if (trip == null) _uiState.value = NavigationUiState.Error("No trip available for navigation")
                    else _uiState.value = NavigationUiState.Success(trip, listOf(LatLng(trip.pickupLat, trip.pickupLng), LatLng(trip.dropLat, trip.dropLng)))
                }
                .onFailure { _uiState.value = NavigationUiState.Error(it.message ?: "Unable to load map") }
        }
    }
}
