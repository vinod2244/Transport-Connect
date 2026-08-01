package com.aptransportconnect.driver.data.local.database

import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.aptransportconnect.driver.data.local.database.dao.DocumentDao
import com.aptransportconnect.driver.data.local.database.dao.NotificationDao
import com.aptransportconnect.driver.data.local.database.dao.TransactionDao
import com.aptransportconnect.driver.data.local.database.dao.TripDao
import com.aptransportconnect.driver.domain.model.Document
import com.aptransportconnect.driver.domain.model.DocumentStatus
import com.aptransportconnect.driver.domain.model.DocumentType
import com.aptransportconnect.driver.domain.model.DriverNotification
import com.aptransportconnect.driver.domain.model.Transaction
import com.aptransportconnect.driver.domain.model.TransactionType
import com.aptransportconnect.driver.domain.model.Trip
import com.aptransportconnect.driver.domain.model.TripStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey val id: String,
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val pickupAddress: String,
    val dropAddress: String,
    val pickupLat: Double,
    val pickupLng: Double,
    val dropLat: Double,
    val dropLng: Double,
    val estimatedFare: Double,
    val distance: Double,
    val status: String,
    val vehicleType: String,
    val requestedAt: Long,
    val startedAt: Long?,
    val completedAt: Long?,
    val actualFare: Double?
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val body: String,
    val type: String,
    val timestamp: Long,
    val isRead: Boolean,
    val data: Map<String, String>
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val amount: Double,
    val type: String,
    val description: String,
    val timestamp: Long,
    val status: String
)

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey val id: String,
    val type: String,
    val name: String,
    val status: String,
    val expiryDate: String?,
    val uploadedAt: Long?,
    val url: String?
)

fun TripEntity.toDomain() = Trip(id, customerId, customerName, customerPhone, pickupAddress, dropAddress, pickupLat, pickupLng, dropLat, dropLng, estimatedFare, distance, TripStatus.valueOf(status), vehicleType, requestedAt, startedAt, completedAt, actualFare)
fun Trip.toEntity() = TripEntity(id, customerId, customerName, customerPhone, pickupAddress, dropAddress, pickupLat, pickupLng, dropLat, dropLng, estimatedFare, distance, status.name, vehicleType, requestedAt, startedAt, completedAt, actualFare)
fun NotificationEntity.toDomain() = DriverNotification(id, title, body, type, timestamp, isRead, data)
fun DriverNotification.toEntity() = NotificationEntity(id, title, body, type, timestamp, isRead, data)
fun TransactionEntity.toDomain() = Transaction(id, amount, TransactionType.valueOf(type), description, timestamp, status)
fun Transaction.toEntity() = TransactionEntity(id, amount, type.name, description, timestamp, status)
fun DocumentEntity.toDomain() = Document(id, DocumentType.valueOf(type), name, DocumentStatus.valueOf(status), expiryDate, uploadedAt, url)
fun Document.toEntity() = DocumentEntity(id, type.name, name, status.name, expiryDate, uploadedAt, url)

class MapConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromMap(value: Map<String, String>): String = gson.toJson(value)

    @TypeConverter
    fun toMap(value: String): Map<String, String> =
        gson.fromJson(value, object : TypeToken<Map<String, String>>() {}.type) ?: emptyMap()
}

@Database(entities = [TripEntity::class, NotificationEntity::class, TransactionEntity::class, DocumentEntity::class], version = 1, exportSchema = false)
@TypeConverters(MapConverters::class)
abstract class DriverDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun notificationDao(): NotificationDao
    abstract fun transactionDao(): TransactionDao
    abstract fun documentDao(): DocumentDao
}
