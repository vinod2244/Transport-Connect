package com.aptransportconnect.driver.domain.model

data class Wallet(
    val balance: Double,
    val pendingPayout: Double,
    val currency: String = "INR"
)
