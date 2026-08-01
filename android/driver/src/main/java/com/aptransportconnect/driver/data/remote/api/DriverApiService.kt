package com.aptransportconnect.driver.data.remote.api

import com.aptransportconnect.driver.data.remote.dto.DocumentDto
import com.aptransportconnect.driver.data.remote.dto.EarningsDto
import com.aptransportconnect.driver.data.remote.dto.NotificationDto
import com.aptransportconnect.driver.data.remote.dto.ProfileDto
import com.aptransportconnect.driver.data.remote.dto.TransactionDto
import com.aptransportconnect.driver.data.remote.dto.TripDto
import com.aptransportconnect.driver.data.remote.dto.WalletDto
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

data class ApiMessageDto(val message: String)
data class LoginRequestDto(val phone: String)
data class OtpChallengeDto(val message: String, val expiresInSeconds: Int = 60)
data class VerifyOtpRequestDto(val phone: String, val otp: String)
data class RefreshTokenRequestDto(val refreshToken: String)
data class AuthTokenDto(val accessToken: String, val refreshToken: String)
data class UpdateTripStatusRequestDto(val status: String)
data class LocationUpdateRequestDto(val lat: Double, val lng: Double, val heading: Float)
data class WithdrawRequestDto(val amount: Double, val bankAccount: String)
data class AvailabilityRequestDto(val isOnline: Boolean)
data class FcmTokenRequestDto(val token: String)

interface DriverApiService {
    @POST("/driver/login") suspend fun login(@Body body: LoginRequestDto): OtpChallengeDto
    @POST("/driver/verify-otp") suspend fun verifyOtp(@Body body: VerifyOtpRequestDto): AuthTokenDto
    @POST("/driver/refresh-token") suspend fun refreshToken(@Body body: RefreshTokenRequestDto): AuthTokenDto
    @POST("/driver/logout") suspend fun logout(): ApiMessageDto
    @GET("/driver/profile") suspend fun getProfile(): ProfileDto
    @PUT("/driver/profile") suspend fun updateProfile(@Body body: ProfileDto): ProfileDto
    @GET("/driver/trips/requests") suspend fun getTripRequests(): List<TripDto>
    @POST("/driver/trips/{id}/accept") suspend fun acceptTrip(@Path("id") id: String): TripDto
    @POST("/driver/trips/{id}/reject") suspend fun rejectTrip(@Path("id") id: String): ApiMessageDto
    @GET("/driver/trips/current") suspend fun getCurrentTrip(): TripDto?
    @PUT("/driver/trips/{id}/status") suspend fun updateTripStatus(@Path("id") id: String, @Body body: UpdateTripStatusRequestDto): TripDto
    @GET("/driver/trips/history") suspend fun getTripHistory(@Query("page") page: Int = 1, @Query("limit") limit: Int = 50, @Query("date") date: String? = null): List<TripDto>
    @PUT("/driver/location") suspend fun updateLocation(@Body body: LocationUpdateRequestDto): ApiMessageDto
    @GET("/driver/wallet") suspend fun getWallet(): WalletDto
    @GET("/driver/transactions") suspend fun getTransactions(): List<TransactionDto>
    @POST("/driver/withdraw") suspend fun withdraw(@Body body: WithdrawRequestDto): ApiMessageDto
    @GET("/driver/earnings") suspend fun getEarnings(): EarningsDto
    @GET("/driver/earnings/summary") suspend fun getEarningsSummary(): EarningsDto
    @GET("/driver/notifications") suspend fun getNotifications(): List<NotificationDto>
    @PUT("/driver/notifications/{id}/read") suspend fun markNotificationRead(@Path("id") id: String): ApiMessageDto
    @GET("/driver/documents") suspend fun getDocuments(): List<DocumentDto>
    @Multipart @POST("/driver/documents/{type}/upload") suspend fun uploadDocument(@Path("type") type: String, @Part file: MultipartBody.Part): ApiMessageDto
    @PUT("/driver/availability") suspend fun updateAvailability(@Body body: AvailabilityRequestDto): ApiMessageDto
    @POST("/driver/fcm-token") suspend fun saveFcmToken(@Body body: FcmTokenRequestDto): ApiMessageDto
}
