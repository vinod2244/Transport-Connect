package com.aptransportconnect.driver.domain.repository

import com.aptransportconnect.driver.domain.model.Transaction
import com.aptransportconnect.driver.domain.model.Wallet
import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    fun observeTransactions(): Flow<List<Transaction>>
    suspend fun getWallet(): Result<Wallet>
    suspend fun refreshTransactions(): Result<List<Transaction>>
    suspend fun withdraw(amount: Double, bankAccount: String): Result<Unit>
}
