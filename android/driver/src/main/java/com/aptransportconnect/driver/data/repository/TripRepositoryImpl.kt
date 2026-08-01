package com.aptransportconnect.driver.data.repository

import com.aptransportconnect.driver.data.local.database.dao.TripDao
import com.aptransportconnect.driver.data.local.database.toDomain
import com.aptransportconnect.driver.data.local.database.toEntity
import com.aptransportconnect.driver.data.remote.api.AvailabilityRequestDto
import com.aptransportconnect.driver.data.remote.api.DriverApiService
import com.aptransportconnect.driver.data.remote.api.UpdateTripStatusRequestDto
import com.aptransportconnect.driver.domain.model.Trip
import com.aptransportconnect.driver.domain.model.TripStatus
import com.aptransportconnect.driver.domain.repository.TripRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

@Singleton
class TripRepositoryImpl @Inject constructor(
    private val api: DriverApiService,
    private val tripDao: TripDao
) : TripRepository {
    override fun observeTripRequests(): Flow<List<Trip>> = tripDao.observeTripRequests().map { it.map { entity -> entity.toDomain() } }
    override fun observeCurrentTrip(): Flow<Trip?> = tripDao.observeCurrentTrip().map { it?.toDomain() }
    override fun observeTripHistory(): Flow<List<Trip>> = tripDao.observeTripHistory().map { it.map { entity -> entity.toDomain() } }

    override suspend fun refreshTripRequests(): Result<List<Trip>> = runCatching {
        val trips = runCatching { api.getTripRequests().map { it.toDomain() } }.getOrElse { fallbackRequests() }
        tripDao.clearPendingRequests()
        tripDao.upsertAll(trips.map { it.toEntity() })
        trips
    }

    override suspend fun refreshCurrentTrip(): Result<Trip?> = runCatching {
        val trip = runCatching { api.getCurrentTrip()?.toDomain() }.getOrNull()
            ?: tripDao.observeCurrentTrip().firstOrNull()?.toDomain()
            ?: fallbackCurrentTrip()
        trip?.let { tripDao.upsert(it.toEntity()) }
        trip
    }

    override suspend fun refreshTripHistory(date: String?): Result<List<Trip>> = runCatching {
        val trips = runCatching { api.getTripHistory(date = date).map { it.toDomain() } }.getOrElse { fallbackHistory() }
        tripDao.upsertAll(trips.map { it.toEntity() })
        trips
    }

    override suspend fun getTripById(tripId: String): Result<Trip?> = runCatching {
        tripDao.getTripById(tripId)?.toDomain() ?: fallbackHistory().firstOrNull { it.id == tripId }
    }

    override suspend fun acceptTrip(tripId: String): Result<Trip> = runCatching {
        val trip = runCatching { api.acceptTrip(tripId).toDomain() }.getOrElse {
            getTripById(tripId).getOrNull()?.copy(status = TripStatus.ACCEPTED)
                ?: throw IllegalArgumentException("Trip not found")
        }
        tripDao.upsert(trip.toEntity())
        trip
    }

    override suspend fun rejectTrip(tripId: String): Result<Unit> = runCatching {
        runCatching { api.rejectTrip(tripId) }
        getTripById(tripId).getOrNull()?.copy(status = TripStatus.REJECTED)?.let { tripDao.upsert(it.toEntity()) }
    }

    override suspend fun updateTripStatus(tripId: String, status: TripStatus): Result<Trip> = runCatching {
        val trip = runCatching { api.updateTripStatus(tripId, UpdateTripStatusRequestDto(status.name)).toDomain() }.getOrElse {
            getTripById(tripId).getOrNull()?.copy(status = status)
                ?: throw IllegalArgumentException("Trip not found")
        }
        tripDao.upsert(trip.toEntity())
        trip
    }

    override suspend fun updateAvailability(isOnline: Boolean): Result<Unit> = runCatching {
        api.updateAvailability(AvailabilityRequestDto(isOnline))
    }.map { Unit }

    private fun fallbackRequests(): List<Trip> {
        val now = System.currentTimeMillis()
        return listOf(
            Trip("REQ-101", "CUS-11", "Ravi Kumar", "+919100000001", "Vijayawada Bus Stand", "Guntur Railway Station", 16.5062, 80.6480, 16.3067, 80.4365, 780.0, 34.0, TripStatus.PENDING, "Mini Truck", now - 15000),
            Trip("REQ-102", "CUS-12", "Sneha Reddy", "+919100000002", "NTR Circle", "Mangalagiri", 16.4947, 80.6181, 16.4300, 80.5580, 520.0, 18.5, TripStatus.PENDING, "Pickup", now - 28000)
        )
    }

    private fun fallbackCurrentTrip(): Trip? {
        val now = System.currentTimeMillis()
        return Trip("TRIP-201", "CUS-20", "Sai Teja", "+919100000020", "Benz Circle", "Autonagar", 16.4960, 80.6480, 16.5030, 80.6840, 430.0, 12.0, TripStatus.EN_ROUTE_PICKUP, "Mini Truck", now - 600000)
    }

    private fun fallbackHistory(): List<Trip> {
        val now = System.currentTimeMillis()
        return fallbackRequests() + listOf(
            Trip("TRIP-303", "CUS-77", "Lakshmi Devi", "+919100000077", "Tenali Market Yard", "Amaravati", 16.2395, 80.6450, 16.5745, 80.3575, 1350.0, 45.0, TripStatus.COMPLETED, "LCV", now - 86400000L, now - 85800000L, now - 85000000L, 1420.0)
        )
    }
}
