package com.aptransportconnect.driver.presentation.auth

import com.aptransportconnect.driver.MainDispatcherRule
import com.aptransportconnect.driver.domain.repository.AuthRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun requestOtp_thenVerify_emitsAuthenticated() = runTest {
        val repository = FakeAuthRepository()
        val viewModel = AuthViewModel(repository)
        viewModel.requestOtp("9999999999")
        advanceUntilIdle()
        assertThat(viewModel.uiState.value).isInstanceOf(AuthUiState.CodeSent::class.java)
        viewModel.verifyOtp("9999999999", "123456")
        advanceUntilIdle()
        assertThat(viewModel.uiState.value).isEqualTo(AuthUiState.Authenticated)
    }

    @Test
    fun refreshSession_success_emitsAuthenticated() = runTest {
        val viewModel = AuthViewModel(FakeAuthRepository())
        viewModel.refreshSession()
        advanceUntilIdle()
        assertThat(viewModel.uiState.value).isEqualTo(AuthUiState.Authenticated)
    }
}

private class FakeAuthRepository : AuthRepository {
    override fun isLoggedIn(): Flow<Boolean> = flowOf(false)
    override suspend fun requestOtp(phone: String): Result<String> = Result.success("OTP sent")
    override suspend fun verifyOtp(phone: String, otp: String): Result<Unit> = Result.success(Unit)
    override suspend fun refreshToken(): Result<Boolean> = Result.success(true)
    override suspend fun logout(): Result<Unit> = Result.success(Unit)
}
