package com.aptransportconnect.driver.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun isLoggedIn(): Flow<Boolean>
    suspend fun requestOtp(phone: String): Result<String>
    suspend fun verifyOtp(phone: String, otp: String): Result<Unit>
    suspend fun refreshToken(): Result<Boolean>
    suspend fun logout(): Result<Unit>
}
