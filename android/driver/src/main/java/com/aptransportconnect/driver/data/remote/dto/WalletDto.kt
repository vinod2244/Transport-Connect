package com.aptransportconnect.driver.data.remote.dto

import com.aptransportconnect.driver.domain.model.Transaction
import com.aptransportconnect.driver.domain.model.TransactionType
import com.aptransportconnect.driver.domain.model.Wallet

data class WalletDto(
    val balance: Double,
    val pendingPayout: Double,
    val currency: String = "INR"
) {
    fun toDomain() = Wallet(balance, pendingPayout, currency)
}

data class TransactionDto(
    val id: String,
    val amount: Double,
    val type: String,
    val description: String,
    val timestamp: Long,
    val status: String
) {
    fun toDomain() = Transaction(
        id = id,
        amount = amount,
        type = runCatching { TransactionType.valueOf(type.uppercase()) }.getOrDefault(TransactionType.CREDIT),
        description = description,
        timestamp = timestamp,
        status = status
    )
}
