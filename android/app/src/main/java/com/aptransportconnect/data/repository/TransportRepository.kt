package com.aptransportconnect.data.repository

import com.aptransportconnect.core.common.ResultState
import com.aptransportconnect.domain.model.Booking
import com.aptransportconnect.domain.model.ChatMessage
import com.aptransportconnect.domain.model.NotificationItem
import com.aptransportconnect.domain.model.PaymentResult
import com.aptransportconnect.domain.model.Profile
import com.aptransportconnect.domain.model.TrackingUpdate
import com.aptransportconnect.domain.model.UserSession
import com.aptransportconnect.domain.model.Vehicle
import kotlinx.coroutines.flow.Flow

interface TransportRepository {
    suspend fun login(mobile: String, password: String, rememberMe: Boolean): ResultState<UserSession>
    suspend fun verifyOtp(mobile: String, otp: String, rememberMe: Boolean): ResultState<UserSession>
    fun onboardingDone(): Flow<Boolean>
    suspend fun setOnboardingDone(done: Boolean)
    fun isLoggedIn(): Flow<Boolean>

    suspend fun searchVehicles(query: String): ResultState<List<Vehicle>>
    suspend fun createBooking(booking: Booking): ResultState<Booking>
    suspend fun bookingDetails(bookingId: String): ResultState<Booking>
    suspend fun refreshBookingHistory(): ResultState<List<Booking>>
    fun observeBookingHistory(): Flow<List<Booking>>

    suspend fun tracking(bookingId: String): ResultState<TrackingUpdate>
    suspend fun pay(bookingId: String, amount: Double): ResultState<PaymentResult>

    suspend fun refreshNotifications(): ResultState<List<NotificationItem>>
    fun observeNotifications(): Flow<List<NotificationItem>>

    suspend fun refreshChat(threadId: String): ResultState<List<ChatMessage>>
    suspend fun sendMessage(threadId: String, text: String): ResultState<ChatMessage>
    fun observeChat(threadId: String): Flow<List<ChatMessage>>

    suspend fun profile(): ResultState<Profile>
    suspend fun logout()
}
