package com.aptransportconnect.driver.data.remote.dto

import com.aptransportconnect.driver.domain.model.DriverNotification

data class NotificationDto(
    val id: String,
    val title: String,
    val body: String,
    val type: String,
    val timestamp: Long,
    val isRead: Boolean,
    val data: Map<String, String> = emptyMap()
) {
    fun toDomain() = DriverNotification(id, title, body, type, timestamp, isRead, data)
}
