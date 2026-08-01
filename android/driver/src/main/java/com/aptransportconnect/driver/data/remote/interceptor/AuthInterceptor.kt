package com.aptransportconnect.driver.data.remote.interceptor

import com.aptransportconnect.driver.data.local.datastore.DriverPreferences
import com.aptransportconnect.driver.data.remote.api.DriverApiService
import com.aptransportconnect.driver.data.remote.api.RefreshTokenRequestDto
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor @Inject constructor(
    private val preferences: DriverPreferences,
    @Named("authlessApi") private val authlessApi: DriverApiService
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = runBlocking { preferences.accessToken.first() }
        val request = if (token.isNullOrBlank()) {
            original
        } else {
            original.newBuilder().header("Authorization", "Bearer " + token).build()
        }

        val response = chain.proceed(request)
        if (response.code != 401 || original.url.encodedPath.endsWith("/driver/refresh-token")) return response
        response.close()

        val refreshToken = runBlocking { preferences.refreshToken.first() } ?: return chain.proceed(request)
        val refreshed = runBlocking {
            runCatching { authlessApi.refreshToken(RefreshTokenRequestDto(refreshToken)) }.getOrNull()
        } ?: return chain.proceed(request)

        runBlocking { preferences.saveAuthTokens(refreshed.accessToken, refreshed.refreshToken) }
        return chain.proceed(
            original.newBuilder()
                .header("Authorization", "Bearer " + refreshed.accessToken)
                .build()
        )
    }
}
