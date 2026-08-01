package com.aptransportconnect.driver.domain.model

data class DriverNotification(
    val id: String,
    val title: String,
    val body: String,
    val type: String,
    val timestamp: Long,
    val isRead: Boolean,
    val data: Map<String, String> = emptyMap()
)
