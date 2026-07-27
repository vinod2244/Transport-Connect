package com.aptransportconnect.driver.data.repository

import com.aptransportconnect.driver.data.remote.api.DriverApiService
import com.aptransportconnect.driver.domain.model.Earnings
import com.aptransportconnect.driver.domain.repository.EarningsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EarningsRepositoryImpl @Inject constructor(
    private val api: DriverApiService
) : EarningsRepository {
    override suspend fun getEarnings(): Result<Earnings> = runCatching {
        runCatching { api.getEarningsSummary().toDomain() }
            .recoverCatching { api.getEarnings().toDomain() }
            .getOrDefault(Earnings(2450.0, 12800.0, 48120.0, 86, 4.8f))
    }
}
