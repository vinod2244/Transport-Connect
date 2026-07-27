package com.aptransportconnect.domain.mapper

import com.aptransportconnect.data.remote.dto.BookingDto
import com.aptransportconnect.data.remote.dto.ChatDto
import com.aptransportconnect.data.remote.dto.NotificationDto
import com.aptransportconnect.data.remote.dto.PaymentDto
import com.aptransportconnect.data.remote.dto.ProfileDto
import com.aptransportconnect.data.remote.dto.TrackingDto
import com.aptransportconnect.data.remote.dto.VehicleDto
import com.aptransportconnect.domain.model.Booking
import com.aptransportconnect.domain.model.ChatMessage
import com.aptransportconnect.domain.model.NotificationItem
import com.aptransportconnect.domain.model.PaymentResult
import com.aptransportconnect.domain.model.PaymentState
import com.aptransportconnect.domain.model.Profile
import com.aptransportconnect.domain.model.TrackingUpdate
import com.aptransportconnect.domain.model.Vehicle

fun VehicleDto.toDomain() = Vehicle(id, name, type, capacityTons, imageUrl)

fun BookingDto.toDomain() = Booking(id, vehicleId, pickup, drop, status, amount, etaMinutes)

fun TrackingDto.toDomain() = TrackingUpdate(bookingId, lat, lng, driverName)

fun PaymentDto.toDomain() = PaymentResult(
    bookingId = bookingId,
    state = when (state.uppercase()) {
        "SUCCESS" -> PaymentState.SUCCESS
        "FAILURE" -> PaymentState.FAILURE
        else -> PaymentState.INITIATED
    },
    transactionId = transactionId,
)

fun NotificationDto.toDomain() = NotificationItem(id, title, message, timestamp)

fun ChatDto.toDomain() = ChatMessage(id, threadId, text, sender, timestamp)

fun ProfileDto.toDomain() = Profile(name, mobile, avatarUrl, email)
