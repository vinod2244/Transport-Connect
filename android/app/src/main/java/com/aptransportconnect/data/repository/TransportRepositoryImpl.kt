package com.aptransportconnect.data.repository

import com.aptransportconnect.core.common.ResultState
import com.aptransportconnect.data.local.dao.BookingDao
import com.aptransportconnect.data.local.dao.ChatDao
import com.aptransportconnect.data.local.dao.NotificationDao
import com.aptransportconnect.data.local.entity.BookingEntity
import com.aptransportconnect.data.local.entity.ChatMessageEntity
import com.aptransportconnect.data.local.entity.NotificationEntity
import com.aptransportconnect.data.remote.dto.BookingDto
import com.aptransportconnect.data.remote.dto.ChatDto
import com.aptransportconnect.data.remote.dto.LoginRequest
import com.aptransportconnect.data.remote.dto.OtpRequest
import com.aptransportconnect.data.remote.dto.PaymentRequest
import com.aptransportconnect.data.remote.service.ApiService
import com.aptransportconnect.domain.mapper.toDomain
import com.aptransportconnect.domain.model.Booking
import com.aptransportconnect.domain.model.ChatMessage
import com.aptransportconnect.domain.model.NotificationItem
import com.aptransportconnect.domain.model.PaymentResult
import com.aptransportconnect.domain.model.Profile
import com.aptransportconnect.domain.model.TrackingUpdate
import com.aptransportconnect.domain.model.UserSession
import com.aptransportconnect.domain.model.Vehicle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class TransportRepositoryImpl(
    private val api: ApiService,
    private val bookingDao: BookingDao,
    private val notificationDao: NotificationDao,
    private val chatDao: ChatDao,
    private val sessionStore: SessionStore,
) : TransportRepository {

    override suspend fun login(mobile: String, password: String, rememberMe: Boolean): ResultState<UserSession> =
        safeApiCall {
            val response = api.login(LoginRequest(mobile, password))
            val session = UserSession(response.token, response.refreshToken, response.role, mobile)
            sessionStore.persistSession(response.token, response.refreshToken, mobile, response.role, rememberMe)
            session
        }.fallback {
            val session = UserSession("mock-token", "mock-refresh", "customer", mobile)
            sessionStore.persistSession(session.token, session.refreshToken, mobile, session.role, rememberMe)
            session
        }

    override suspend fun verifyOtp(mobile: String, otp: String, rememberMe: Boolean): ResultState<UserSession> =
        safeApiCall {
            val response = api.verifyOtp(OtpRequest(mobile, otp))
            val session = UserSession(response.token, response.refreshToken, response.role, mobile)
            sessionStore.persistSession(response.token, response.refreshToken, mobile, response.role, rememberMe)
            session
        }.fallback {
            val session = UserSession("mock-token", "mock-refresh", "customer", mobile)
            sessionStore.persistSession(session.token, session.refreshToken, mobile, session.role, rememberMe)
            session
        }

    override fun onboardingDone(): Flow<Boolean> = sessionStore.onboardingDone

    override suspend fun setOnboardingDone(done: Boolean) = sessionStore.setOnboardingDone(done)

    override fun isLoggedIn(): Flow<Boolean> = sessionStore.authToken.map { it.isNotBlank() }

    override suspend fun searchVehicles(query: String): ResultState<List<Vehicle>> = safeApiCall {
        api.searchVehicles(query).map { it.toDomain() }
    }.fallback {
        listOf(
            Vehicle("1", "Ashok Leyland 12T", "Truck", 12.0, "https://picsum.photos/300/200"),
            Vehicle("2", "Tata Ace", "Mini Truck", 1.0, "https://picsum.photos/300/201"),
        ).filter { it.name.contains(query, true) || query.isBlank() }
    }

    override suspend fun createBooking(booking: Booking): ResultState<Booking> = safeApiCall {
        val created = api.createBooking(booking.toDto()).toDomain()
        bookingDao.upsert(created.toEntity())
        created
    }.fallback {
        val created = booking.copy(id = if (booking.id.isBlank()) "BK-${System.currentTimeMillis()}" else booking.id)
        bookingDao.upsert(created.toEntity())
        created
    }

    override suspend fun bookingDetails(bookingId: String): ResultState<Booking> = safeApiCall {
        api.getBooking(bookingId).toDomain().also { bookingDao.upsert(it.toEntity()) }
    }.fallback {
        bookingDao.getById(bookingId)?.toDomain()
            ?: Booking(bookingId, "1", "Hyderabad", "Vijayawada", "CONFIRMED", 2500.0, 95)
    }

    override suspend fun refreshBookingHistory(): ResultState<List<Booking>> = safeApiCall {
        val bookings = api.getBookingHistory().map { it.toDomain() }
        bookingDao.upsertAll(bookings.map { it.toEntity() })
        bookings
    }.fallback {
        val cached = bookingDao.observeBookings().first().map { it.toDomain() }
        cached.ifEmpty {
            listOf(Booking("BK-101", "1", "Hyderabad", "Warangal", "COMPLETED", 3200.0, 0))
        }
    }

    override fun observeBookingHistory(): Flow<List<Booking>> =
        bookingDao.observeBookings().map { it.map(BookingEntity::toDomain) }

    override suspend fun tracking(bookingId: String): ResultState<TrackingUpdate> = safeApiCall {
        api.getTracking(bookingId).toDomain()
    }.fallback {
        TrackingUpdate(bookingId, 17.3850, 78.4867, "Ravi Kumar")
    }

    override suspend fun pay(bookingId: String, amount: Double): ResultState<PaymentResult> = safeApiCall {
        api.makePayment(PaymentRequest(bookingId, amount)).toDomain()
    }.fallback {
        PaymentResult(bookingId, com.aptransportconnect.domain.model.PaymentState.SUCCESS, "TXN-${System.currentTimeMillis()}")
    }

    override suspend fun refreshNotifications(): ResultState<List<NotificationItem>> = safeApiCall {
        val list = api.notifications().map { it.toDomain() }
        notificationDao.upsertAll(list.map { it.toEntity() })
        list
    }.fallback {
        val local = notificationDao.observeNotifications().first().map { it.toDomain() }
        local.ifEmpty {
            listOf(
                NotificationItem(
                    id = "N1",
                    title = "Booking confirmed",
                    message = "Your booking BK-101 has been confirmed",
                    timestamp = System.currentTimeMillis(),
                ),
            )
        }
    }

    override fun observeNotifications(): Flow<List<NotificationItem>> =
        notificationDao.observeNotifications().map { it.map(NotificationEntity::toDomain) }

    override suspend fun refreshChat(threadId: String): ResultState<List<ChatMessage>> = safeApiCall {
        val list = api.thread(threadId).map { it.toDomain() }
        chatDao.upsertAll(list.map { it.toEntity() })
        list
    }.fallback {
        val local = chatDao.observeThread(threadId).first().map { it.toDomain() }
        local.ifEmpty {
            listOf(ChatMessage("C1", threadId, "Driver is 10 mins away", "driver", System.currentTimeMillis()))
        }
    }

    override suspend fun sendMessage(threadId: String, text: String): ResultState<ChatMessage> = safeApiCall {
        val dto = ChatDto("", threadId, text, "customer", System.currentTimeMillis())
        api.sendMessage(threadId, dto).toDomain().also { chatDao.upsert(it.toEntity()) }
    }.fallback {
        ChatMessage("C-${System.currentTimeMillis()}", threadId, text, "customer", System.currentTimeMillis())
            .also { chatDao.upsert(it.toEntity()) }
    }

    override fun observeChat(threadId: String): Flow<List<ChatMessage>> =
        chatDao.observeThread(threadId).map { it.map(ChatMessageEntity::toDomain) }

    override suspend fun profile(): ResultState<Profile> = safeApiCall {
        api.profile().toDomain()
    }.fallback {
        Profile("Customer", "9999999999", "https://picsum.photos/120", "customer@example.com")
    }

    override suspend fun logout() {
        bookingDao.clear()
        notificationDao.clear()
        chatDao.clear()
        sessionStore.clearSession()
    }
}

private suspend fun <T> safeApiCall(block: suspend () -> T): ResultState<T> =
    try {
        ResultState.Success(block())
    } catch (e: Exception) {
        ResultState.Error(e.message ?: "Unknown error", e)
    }

private suspend fun <T> ResultState<T>.fallback(block: suspend () -> T): ResultState<T> = when (this) {
    is ResultState.Success -> this
    is ResultState.Loading -> this
    is ResultState.Error -> try {
        ResultState.Success(block())
    } catch (e: Exception) {
        this
    }
}

private fun Booking.toEntity() = BookingEntity(id, vehicleId, pickup, drop, status, amount, etaMinutes)
private fun BookingEntity.toDomain() = Booking(id, vehicleId, pickup, drop, status, amount, etaMinutes)
private fun NotificationItem.toEntity() = NotificationEntity(id, title, message, timestamp)
private fun NotificationEntity.toDomain() = NotificationItem(id, title, message, timestamp)
private fun ChatMessage.toEntity() = ChatMessageEntity(id, threadId, text, sender, timestamp)
private fun ChatMessageEntity.toDomain() = ChatMessage(id, threadId, text, sender, timestamp)
private fun Booking.toDto() = BookingDto(id, vehicleId, pickup, drop, status, amount, etaMinutes)
