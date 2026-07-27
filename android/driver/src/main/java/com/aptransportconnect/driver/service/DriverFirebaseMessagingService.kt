package com.aptransportconnect.driver.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.aptransportconnect.driver.domain.model.DriverNotification
import com.aptransportconnect.driver.domain.repository.NotificationRepository
import com.aptransportconnect.driver.domain.repository.ProfileRepository
import com.aptransportconnect.driver.utils.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DriverFirebaseMessagingService : FirebaseMessagingService() {
    @Inject lateinit var profileRepository: ProfileRepository
    @Inject lateinit var notificationRepository: NotificationRepository
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        scope.launch { profileRepository.saveFcmToken(token) }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: message.data["title"].orEmpty().ifBlank { "AP Transport Driver" }
        val body = message.notification?.body ?: message.data["body"].orEmpty().ifBlank { "New update received" }
        val notification = DriverNotification(
            id = message.messageId ?: System.currentTimeMillis().toString(),
            title = title,
            body = body,
            type = message.data["type"] ?: "general",
            timestamp = System.currentTimeMillis(),
            isRead = false,
            data = message.data
        )
        scope.launch { notificationRepository.saveNotification(notification) }
        showNotification(notification)
    }

    private fun showNotification(notification: DriverNotification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, "Driver Notifications", NotificationManager.IMPORTANCE_HIGH)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        getSystemService(NotificationManager::class.java).notify(
            notification.id.hashCode(),
            NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(notification.title)
                .setContentText(notification.body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(notification.body))
                .setAutoCancel(true)
                .build()
        )
    }
}
