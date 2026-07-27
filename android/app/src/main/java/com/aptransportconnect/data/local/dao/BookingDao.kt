package com.aptransportconnect.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aptransportconnect.data.local.entity.BookingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings ORDER BY id DESC")
    fun observeBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): BookingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<BookingEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: BookingEntity)

    @Query("DELETE FROM bookings")
    suspend fun clear()
}
