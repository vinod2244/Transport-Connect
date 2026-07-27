package com.aptransportconnect.presentation.viewmodel

import com.aptransportconnect.core.common.ResultState
import com.aptransportconnect.data.repository.TransportRepository
import com.aptransportconnect.domain.model.Booking
import com.aptransportconnect.domain.model.ChatMessage
import com.aptransportconnect.domain.model.NotificationItem
import com.aptransportconnect.domain.model.PaymentResult
import com.aptransportconnect.domain.model.PaymentState
import com.aptransportconnect.domain.model.Profile
import com.aptransportconnect.domain.model.TrackingUpdate
import com.aptransportconnect.domain.model.UserSession
import com.aptransportconnect.domain.model.Vehicle
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelsTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `splash routes to onboarding when first launch`() = runTest {
        val vm = SplashViewModel(FakeRepository(onboarding = false, loggedIn = false))
        dispatcher.scheduler.advanceUntilIdle()

        assertThat(vm.state.value.nextRoute).isEqualTo("onboarding")
    }

    @Test
    fun `auth login sets logged in`() = runTest {
        val vm = AuthViewModel(FakeRepository())
        vm.login("9999999999", "password", true)
        dispatcher.scheduler.advanceUntilIdle()

        assertThat(vm.state.value.loggedIn).isTrue()
    }
}

private class FakeRepository(
    onboarding: Boolean = true,
    loggedIn: Boolean = true,
) : TransportRepository {
    private val onboardingFlow = MutableStateFlow(onboarding)
    private val loggedInFlow = MutableStateFlow(loggedIn)

    override suspend fun login(mobile: String, password: String, rememberMe: Boolean): ResultState<UserSession> =
        ResultState.Success(UserSession("t", "r", "customer", mobile))

    override suspend fun verifyOtp(mobile: String, otp: String, rememberMe: Boolean): ResultState<UserSession> =
        ResultState.Success(UserSession("t", "r", "customer", mobile))

    override fun onboardingDone(): Flow<Boolean> = onboardingFlow
    override suspend fun setOnboardingDone(done: Boolean) {
        onboardingFlow.value = done
    }

    override fun isLoggedIn(): Flow<Boolean> = loggedInFlow
    override suspend fun searchVehicles(query: String): ResultState<List<Vehicle>> = ResultState.Success(emptyList())
    override suspend fun createBooking(booking: Booking): ResultState<Booking> = ResultState.Success(booking)
    override suspend fun bookingDetails(bookingId: String): ResultState<Booking> = ResultState.Success(
        Booking(bookingId, "1", "a", "b", "status", 1.0, 1),
    )

    override suspend fun refreshBookingHistory(): ResultState<List<Booking>> = ResultState.Success(emptyList())
    override fun observeBookingHistory(): Flow<List<Booking>> = flowOf(emptyList())
    override suspend fun tracking(bookingId: String): ResultState<TrackingUpdate> =
        ResultState.Success(TrackingUpdate(bookingId, 0.0, 0.0, "driver"))

    override suspend fun pay(bookingId: String, amount: Double): ResultState<PaymentResult> =
        ResultState.Success(PaymentResult(bookingId, PaymentState.SUCCESS, "txn"))

    override suspend fun refreshNotifications(): ResultState<List<NotificationItem>> = ResultState.Success(emptyList())
    override fun observeNotifications(): Flow<List<NotificationItem>> = flowOf(emptyList())
    override suspend fun refreshChat(threadId: String): ResultState<List<ChatMessage>> = ResultState.Success(emptyList())
    override suspend fun sendMessage(threadId: String, text: String): ResultState<ChatMessage> =
        ResultState.Success(ChatMessage("1", threadId, text, "customer", 0))

    override fun observeChat(threadId: String): Flow<List<ChatMessage>> = flowOf(emptyList())
    override suspend fun profile(): ResultState<Profile> =
        ResultState.Success(Profile("Customer", "9", "", "c@e.com"))

    override suspend fun logout() {
        loggedInFlow.value = false
    }
}
