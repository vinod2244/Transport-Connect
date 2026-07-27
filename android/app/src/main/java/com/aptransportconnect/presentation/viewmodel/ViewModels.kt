package com.aptransportconnect.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aptransportconnect.core.common.ResultState
import com.aptransportconnect.data.repository.TransportRepository
import com.aptransportconnect.domain.model.Booking
import com.aptransportconnect.domain.model.ChatMessage
import com.aptransportconnect.domain.model.NotificationItem
import com.aptransportconnect.domain.model.PaymentResult
import com.aptransportconnect.domain.model.PaymentState
import com.aptransportconnect.domain.model.Profile
import com.aptransportconnect.domain.model.TrackingUpdate
import com.aptransportconnect.domain.model.Vehicle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class SplashState(val isLoading: Boolean = true, val nextRoute: String = "")
class SplashViewModel(private val repository: TransportRepository) : ViewModel() {
    private val _state = MutableStateFlow(SplashState())
    val state: StateFlow<SplashState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val onboardingDone = repository.onboardingDone().first()
            val loggedIn = repository.isLoggedIn().first()
            _state.value = SplashState(false, when {
                !onboardingDone -> "onboarding"
                !loggedIn -> "login"
                else -> "home"
            })
        }
    }
}

data class AuthState(
    val mobile: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val loggedIn: Boolean = false,
)

class AuthViewModel(private val repository: TransportRepository) : ViewModel() {
    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    fun login(mobile: String, password: String, rememberMe: Boolean) {
        _state.update { it.copy(loading = true, error = null, mobile = mobile) }
        viewModelScope.launch {
            when (val result = repository.login(mobile, password, rememberMe)) {
                is ResultState.Success -> _state.update { it.copy(loading = false, loggedIn = true) }
                is ResultState.Error -> _state.update { it.copy(loading = false, error = result.message) }
                ResultState.Loading -> Unit
            }
        }
    }

    fun verifyOtp(mobile: String, otp: String, rememberMe: Boolean) {
        _state.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            when (val result = repository.verifyOtp(mobile, otp, rememberMe)) {
                is ResultState.Success -> _state.update { it.copy(loading = false, loggedIn = true) }
                is ResultState.Error -> _state.update { it.copy(loading = false, error = result.message) }
                ResultState.Loading -> Unit
            }
        }
    }
}

class HomeViewModel : ViewModel()

class SearchVehicleViewModel(private val repository: TransportRepository) : ViewModel() {
    private val _vehicles = MutableStateFlow<List<Vehicle>>(emptyList())
    val vehicles = _vehicles.asStateFlow()

    fun search(query: String) {
        viewModelScope.launch {
            when (val result = repository.searchVehicles(query)) {
                is ResultState.Success -> _vehicles.value = result.data
                else -> Unit
            }
        }
    }
}

class BookingViewModel(private val repository: TransportRepository) : ViewModel() {
    private val _current = MutableStateFlow<Booking?>(null)
    val current = _current.asStateFlow()

    fun create(vehicleId: String, pickup: String, drop: String, amount: Double, etaMinutes: Int) {
        val booking = Booking("", vehicleId, pickup, drop, "INITIATED", amount, etaMinutes)
        viewModelScope.launch {
            when (val result = repository.createBooking(booking)) {
                is ResultState.Success -> _current.value = result.data
                else -> Unit
            }
        }
    }
}

class BookingDetailsViewModel(private val repository: TransportRepository) : ViewModel() {
    private val _state = MutableStateFlow<Booking?>(null)
    val state = _state.asStateFlow()

    fun load(id: String) {
        viewModelScope.launch {
            when (val result = repository.bookingDetails(id)) {
                is ResultState.Success -> _state.value = result.data
                else -> Unit
            }
        }
    }
}

class TrackingViewModel(private val repository: TransportRepository) : ViewModel() {
    private val _tracking = MutableStateFlow<TrackingUpdate?>(null)
    val tracking = _tracking.asStateFlow()

    fun load(id: String) {
        viewModelScope.launch {
            while (isActive) {
                when (val result = repository.tracking(id)) {
                    is ResultState.Success -> _tracking.value = result.data
                    else -> Unit
                }
                delay(TRACKING_REFRESH_INTERVAL_MS)
            }
        }
    }

    private companion object {
        const val TRACKING_REFRESH_INTERVAL_MS = 10_000L
    }
}

class PaymentViewModel(private val repository: TransportRepository) : ViewModel() {
    private val _result = MutableStateFlow<PaymentResult?>(PaymentResult("", PaymentState.INITIATED, null))
    val result = _result.asStateFlow()

    fun pay(bookingId: String, amount: Double) {
        viewModelScope.launch {
            when (val result = repository.pay(bookingId, amount)) {
                is ResultState.Success -> _result.value = result.data
                else -> _result.value = PaymentResult(bookingId, PaymentState.FAILURE, null)
            }
        }
    }
}

class BookingHistoryViewModel(private val repository: TransportRepository) : ViewModel() {
    private val _items = MutableStateFlow<List<Booking>>(emptyList())
    val items = _items.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeBookingHistory().collect { _items.value = it }
        }
        refresh()
    }

    fun refresh() {
        viewModelScope.launch { repository.refreshBookingHistory() }
    }
}

class NotificationsViewModel(private val repository: TransportRepository) : ViewModel() {
    private val _items = MutableStateFlow<List<NotificationItem>>(emptyList())
    val items = _items.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeNotifications().collect { _items.value = it }
        }
        refresh()
    }

    fun refresh() {
        viewModelScope.launch { repository.refreshNotifications() }
    }
}

class ChatViewModel(
    private val repository: TransportRepository,
    private val threadId: String,
) : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()
    init {
        viewModelScope.launch {
            repository.observeChat(threadId).collect { _messages.value = it }
        }
        refresh()
    }

    fun refresh() {
        viewModelScope.launch { repository.refreshChat(threadId) }
    }

    fun send(text: String) {
        viewModelScope.launch { repository.sendMessage(threadId, text) }
    }
}

class ProfileViewModel(private val repository: TransportRepository) : ViewModel() {
    private val _profile = MutableStateFlow<Profile?>(null)
    val profile = _profile.asStateFlow()

    init {
        viewModelScope.launch {
            when (val result = repository.profile()) {
                is ResultState.Success -> _profile.value = result.data
                else -> Unit
            }
        }
    }
}

class SettingsViewModel(private val repository: TransportRepository) : ViewModel() {
    fun logout() {
        viewModelScope.launch { repository.logout() }
    }
}

@Suppress("UNCHECKED_CAST")
fun <T : ViewModel> vmFactory(create: () -> T): ViewModelProvider.Factory =
    object : ViewModelProvider.Factory {
        override fun <VM : ViewModel> create(modelClass: Class<VM>): VM = create() as VM
    }
