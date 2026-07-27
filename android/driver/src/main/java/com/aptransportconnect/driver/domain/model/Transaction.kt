package com.aptransportconnect.driver.domain.model

data class Transaction(
    val id: String,
    val amount: Double,
    val type: TransactionType,
    val description: String,
    val timestamp: Long,
    val status: String
)

enum class TransactionType { CREDIT, DEBIT, WITHDRAWAL }
