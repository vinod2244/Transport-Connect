package com.aptransportconnect.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey val id: String,
    val vehicleId: String,
    val pickup: String,
    val drop: String,
    val status: String,
    val amount: Double,
    val etaMinutes: Int,
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val timestamp: Long,
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val threadId: String,
    val text: String,
    val sender: String,
    val timestamp: Long,
)
