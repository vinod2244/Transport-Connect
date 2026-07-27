package com.aptransportconnect.driver.presentation.earnings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aptransportconnect.driver.domain.model.Earnings
import com.aptransportconnect.driver.domain.usecase.earnings.GetEarningsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface EarningsUiState {
    data object Loading : EarningsUiState
    data class Success(val earnings: Earnings, val averagePerTrip: Double) : EarningsUiState
    data class Error(val message: String) : EarningsUiState
}

@HiltViewModel
class EarningsViewModel @Inject constructor(
    private val getEarningsUseCase: GetEarningsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<EarningsUiState>(EarningsUiState.Loading)
    val uiState: StateFlow<EarningsUiState> = _uiState.asStateFlow()

    init { loadEarnings() }

    fun loadEarnings() {
        viewModelScope.launch {
            _uiState.value = EarningsUiState.Loading
            getEarningsUseCase()
                .onSuccess { _uiState.value = EarningsUiState.Success(it, calculateAveragePerTrip(it)) }
                .onFailure { _uiState.value = EarningsUiState.Error(it.message ?: "Unable to load earnings") }
        }
    }

    fun calculateAveragePerTrip(earnings: Earnings): Double = if (earnings.totalTrips == 0) 0.0 else earnings.month / earnings.totalTrips
}
