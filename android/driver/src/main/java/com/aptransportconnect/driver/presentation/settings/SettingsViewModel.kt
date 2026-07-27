package com.aptransportconnect.driver.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aptransportconnect.driver.data.local.datastore.DriverPreferences
import com.aptransportconnect.driver.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class SettingsState(val notificationsEnabled: Boolean, val isDarkTheme: Boolean, val isOnline: Boolean)

sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(val settings: SettingsState) : SettingsUiState
    data class Error(val message: String) : SettingsUiState
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferences: DriverPreferences,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(preferences.notificationsEnabled, preferences.isDarkTheme, preferences.isOnline) { notifications, dark, online ->
                SettingsState(notifications, dark, online)
            }.collect { _uiState.value = SettingsUiState.Success(it) }
        }
    }

    fun setNotifications(enabled: Boolean) { viewModelScope.launch { preferences.setNotificationsEnabled(enabled) } }
    fun setTheme(isDark: Boolean) { viewModelScope.launch { preferences.setDarkTheme(isDark) } }
    fun setOnline(isOnline: Boolean) { viewModelScope.launch { preferences.setOnlineStatus(isOnline) } }
    fun logout(onLoggedOut: () -> Unit) { viewModelScope.launch { authRepository.logout(); onLoggedOut() } }
}
