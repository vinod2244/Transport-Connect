package com.aptransportconnect.driver.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aptransportconnect.driver.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data class CodeSent(val message: String) : AuthUiState
    data object Authenticated : AuthUiState
    data class Error(val message: String) : AuthUiState
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun requestOtp(phone: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.requestOtp(phone)
                .onSuccess { _uiState.value = AuthUiState.CodeSent(it) }
                .onFailure { _uiState.value = AuthUiState.Error(it.message ?: "Unable to send OTP") }
        }
    }

    fun verifyOtp(phone: String, otp: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.verifyOtp(phone, otp)
                .onSuccess { _uiState.value = AuthUiState.Authenticated }
                .onFailure { _uiState.value = AuthUiState.Error(it.message ?: "OTP verification failed") }
        }
    }

    fun refreshSession() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.refreshToken()
                .onSuccess { _uiState.value = AuthUiState.Authenticated }
                .onFailure { _uiState.value = AuthUiState.Error(it.message ?: "Session refresh failed") }
        }
    }
}
