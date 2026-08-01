package com.aptransportconnect.driver.domain.model

data class Earnings(
    val today: Double,
    val week: Double,
    val month: Double,
    val totalTrips: Int,
    val rating: Float
)
