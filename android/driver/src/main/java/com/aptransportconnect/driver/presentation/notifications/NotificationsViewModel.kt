package com.aptransportconnect.driver.presentation.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aptransportconnect.driver.domain.model.DriverNotification
import com.aptransportconnect.driver.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface NotificationsUiState {
    data object Loading : NotificationsUiState
    data class Success(val notifications: List<DriverNotification>) : NotificationsUiState
    data class Error(val message: String) : NotificationsUiState
}

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<NotificationsUiState>(NotificationsUiState.Loading)
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = NotificationsUiState.Loading
            notificationRepository.refreshNotifications()
                .onSuccess { _uiState.value = NotificationsUiState.Success(it) }
                .onFailure { _uiState.value = NotificationsUiState.Error(it.message ?: "Unable to load notifications") }
        }
    }

    fun markRead(id: String) {
        viewModelScope.launch { notificationRepository.markRead(id); refresh() }
    }

    fun markAllRead() {
        viewModelScope.launch { notificationRepository.markAllRead(); refresh() }
    }
}
