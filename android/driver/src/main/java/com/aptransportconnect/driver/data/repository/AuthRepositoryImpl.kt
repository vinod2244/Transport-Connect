package com.aptransportconnect.driver.data.repository

import com.aptransportconnect.driver.data.local.datastore.DriverPreferences
import com.aptransportconnect.driver.data.remote.api.DriverApiService
import com.aptransportconnect.driver.data.remote.api.LoginRequestDto
import com.aptransportconnect.driver.data.remote.api.RefreshTokenRequestDto
import com.aptransportconnect.driver.data.remote.api.VerifyOtpRequestDto
import com.aptransportconnect.driver.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: DriverApiService,
    private val preferences: DriverPreferences
) : AuthRepository {
    override fun isLoggedIn(): Flow<Boolean> = preferences.isLoggedIn

    override suspend fun requestOtp(phone: String): Result<String> = runCatching {
        preferences.savePhone(phone)
        api.login(LoginRequestDto(phone)).message
    }

    override suspend fun verifyOtp(phone: String, otp: String): Result<Unit> = runCatching {
        val tokens = api.verifyOtp(VerifyOtpRequestDto(phone, otp))
        preferences.saveAuthTokens(tokens.accessToken, tokens.refreshToken)
    }

    override suspend fun refreshToken(): Result<Boolean> = runCatching {
        val refreshToken = preferences.refreshToken.first().orEmpty()
        val tokens = api.refreshToken(RefreshTokenRequestDto(refreshToken))
        preferences.saveAuthTokens(tokens.accessToken, tokens.refreshToken)
        true
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        runCatching { api.logout() }
        preferences.clearAuth()
    }
}
