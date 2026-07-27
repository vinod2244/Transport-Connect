package com.aptransportconnect.data.remote.service

import com.aptransportconnect.data.remote.dto.BookingDto
import com.aptransportconnect.data.remote.dto.ChatDto
import com.aptransportconnect.data.remote.dto.LoginRequest
import com.aptransportconnect.data.remote.dto.NotificationDto
import com.aptransportconnect.data.remote.dto.OtpRequest
import com.aptransportconnect.data.remote.dto.PaymentDto
import com.aptransportconnect.data.remote.dto.PaymentRequest
import com.aptransportconnect.data.remote.dto.ProfileDto
import com.aptransportconnect.data.remote.dto.TokenResponse
import com.aptransportconnect.data.remote.dto.TrackingDto
import com.aptransportconnect.data.remote.dto.VehicleDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): TokenResponse

    @POST("auth/otp/verify")
    suspend fun verifyOtp(@Body request: OtpRequest): TokenResponse

    @GET("vehicles")
    suspend fun searchVehicles(@Query("q") query: String): List<VehicleDto>

    @POST("bookings")
    suspend fun createBooking(@Body booking: BookingDto): BookingDto

    @GET("bookings/{id}")
    suspend fun getBooking(@Path("id") id: String): BookingDto

    @GET("bookings")
    suspend fun getBookingHistory(): List<BookingDto>

    @GET("tracking/{bookingId}")
    suspend fun getTracking(@Path("bookingId") bookingId: String): TrackingDto

    @POST("payments")
    suspend fun makePayment(@Body request: PaymentRequest): PaymentDto

    @GET("notifications")
    suspend fun notifications(): List<NotificationDto>

    @GET("chat/{threadId}")
    suspend fun thread(@Path("threadId") threadId: String): List<ChatDto>

    @POST("chat/{threadId}")
    suspend fun sendMessage(@Path("threadId") threadId: String, @Body message: ChatDto): ChatDto

    @GET("profile")
    suspend fun profile(): ProfileDto
}
