package com.aptransportconnect.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aptransportconnect.data.local.dao.BookingDao
import com.aptransportconnect.data.local.dao.ChatDao
import com.aptransportconnect.data.local.dao.NotificationDao
import com.aptransportconnect.data.local.entity.BookingEntity
import com.aptransportconnect.data.local.entity.ChatMessageEntity
import com.aptransportconnect.data.local.entity.NotificationEntity

@Database(
    entities = [BookingEntity::class, NotificationEntity::class, ChatMessageEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookingDao(): BookingDao
    abstract fun notificationDao(): NotificationDao
    abstract fun chatDao(): ChatDao
}
