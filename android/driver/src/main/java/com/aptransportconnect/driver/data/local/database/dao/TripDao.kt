package com.aptransportconnect.driver.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aptransportconnect.driver.data.local.database.TripEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trips WHERE status = 'PENDING' ORDER BY requestedAt ASC")
    fun observeTripRequests(): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE status NOT IN ('COMPLETED', 'CANCELLED', 'REJECTED') ORDER BY requestedAt DESC LIMIT 1")
    fun observeCurrentTrip(): Flow<TripEntity?>

    @Query("SELECT * FROM trips ORDER BY requestedAt DESC")
    fun observeTripHistory(): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE id = :tripId LIMIT 1")
    suspend fun getTripById(tripId: String): TripEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(trips: List<TripEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(trip: TripEntity)

    @Query("DELETE FROM trips WHERE status = 'PENDING'")
    suspend fun clearPendingRequests()
}
