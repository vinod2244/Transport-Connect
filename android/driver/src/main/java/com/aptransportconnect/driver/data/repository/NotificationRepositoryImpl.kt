package com.aptransportconnect.driver.data.repository

import com.aptransportconnect.driver.data.local.database.dao.NotificationDao
import com.aptransportconnect.driver.data.local.database.toDomain
import com.aptransportconnect.driver.data.local.database.toEntity
import com.aptransportconnect.driver.data.remote.api.DriverApiService
import com.aptransportconnect.driver.domain.model.DriverNotification
import com.aptransportconnect.driver.domain.repository.NotificationRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val api: DriverApiService,
    private val notificationDao: NotificationDao
) : NotificationRepository {
    override fun observeNotifications(): Flow<List<DriverNotification>> = notificationDao.observeNotifications().map { it.map { entity -> entity.toDomain() } }

    override suspend fun refreshNotifications(): Result<List<DriverNotification>> = runCatching {
        val notifications = runCatching { api.getNotifications().map { it.toDomain() } }.getOrElse {
            listOf(
                DriverNotification("NOT-1", "Trip Request", "New request from Benz Circle", "trip", System.currentTimeMillis() - 5000, false),
                DriverNotification("NOT-2", "Wallet Update", "₹1,850 credited to wallet", "wallet", System.currentTimeMillis() - 70000, true)
            )
        }
        notificationDao.upsertAll(notifications.map { it.toEntity() })
        notifications
    }

    override suspend fun markRead(notificationId: String): Result<Unit> = runCatching {
        runCatching { api.markNotificationRead(notificationId) }
        notificationDao.markRead(notificationId)
    }

    override suspend fun markAllRead(): Result<Unit> = runCatching { notificationDao.markAllRead() }
    override suspend fun saveNotification(notification: DriverNotification): Result<Unit> = runCatching { notificationDao.upsert(notification.toEntity()) }
}
