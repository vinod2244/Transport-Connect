package com.aptransportconnect.driver.domain.repository

import com.aptransportconnect.driver.domain.model.DriverNotification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun observeNotifications(): Flow<List<DriverNotification>>
    suspend fun refreshNotifications(): Result<List<DriverNotification>>
    suspend fun markRead(notificationId: String): Result<Unit>
    suspend fun markAllRead(): Result<Unit>
    suspend fun saveNotification(notification: DriverNotification): Result<Unit>
}
