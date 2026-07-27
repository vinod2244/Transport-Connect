package com.aptransportconnect.driver.domain.repository

import com.aptransportconnect.driver.domain.model.Earnings

interface EarningsRepository {
    suspend fun getEarnings(): Result<Earnings>
}
