package com.aptransportconnect.driver.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aptransportconnect.driver.data.local.datastore.DriverPreferences
import com.aptransportconnect.driver.domain.model.DriverProfile
import com.aptransportconnect.driver.domain.model.Earnings
import com.aptransportconnect.driver.domain.model.Trip
import com.aptransportconnect.driver.domain.repository.ProfileRepository
import com.aptransportconnect.driver.domain.usecase.earnings.GetEarningsUseCase
import com.aptransportconnect.driver.domain.usecase.trip.GetCurrentTripUseCase
import com.aptransportconnect.driver.domain.usecase.trip.GetTripRequestsUseCase
import com.aptransportconnect.driver.domain.usecase.trip.UpdateAvailabilityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Success(
        val pendingRequests: Int,
        val earnings: Earnings,
        val profile: DriverProfile,
        val currentTrip: Trip?
    ) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getTripRequestsUseCase: GetTripRequestsUseCase,
    private val updateAvailabilityUseCase: UpdateAvailabilityUseCase,
    private val getEarningsUseCase: GetEarningsUseCase,
    private val getCurrentTripUseCase: GetCurrentTripUseCase,
    private val profileRepository: ProfileRepository,
    private val preferences: DriverPreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _isOnline = MutableStateFlow(false)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    init {
        viewModelScope.launch { preferences.isOnline.collect { _isOnline.value = it } }
        loadDashboard()
    }

    fun toggleAvailability() {
        viewModelScope.launch {
            val next = !_isOnline.value
            updateAvailabilityUseCase(next)
                .onSuccess { preferences.setOnlineStatus(next) }
                .onFailure { _uiState.value = DashboardUiState.Error(it.message ?: "Unable to update availability") }
        }
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            val requests = getTripRequestsUseCase().getOrElse { emptyList() }
            val earnings = getEarningsUseCase().getOrElse { Earnings(0.0, 0.0, 0.0, 0, 0f) }
            val profile = profileRepository.getProfile().getOrElse {
                DriverProfile("", "Driver", preferences.phone.first().orEmpty(), "", "", "", 0f, 0, false, null)
            }
            val currentTrip = getCurrentTripUseCase().getOrElse { null }
            _uiState.value = DashboardUiState.Success(requests.size, earnings, profile, currentTrip)
        }
    }
}
