package com.aptransportconnect.driver.data.remote.dto

import com.aptransportconnect.driver.domain.model.Earnings

data class EarningsDto(
    val today: Double,
    val week: Double,
    val month: Double,
    val totalTrips: Int,
    val rating: Float
) {
    fun toDomain() = Earnings(today, week, month, totalTrips, rating)
}
