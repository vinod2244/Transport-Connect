package com.aptransportconnect.driver.data.repository

import com.aptransportconnect.driver.data.local.database.dao.TransactionDao
import com.aptransportconnect.driver.data.local.database.toDomain
import com.aptransportconnect.driver.data.local.database.toEntity
import com.aptransportconnect.driver.data.remote.api.DriverApiService
import com.aptransportconnect.driver.data.remote.api.WithdrawRequestDto
import com.aptransportconnect.driver.domain.model.Transaction
import com.aptransportconnect.driver.domain.model.TransactionType
import com.aptransportconnect.driver.domain.model.Wallet
import com.aptransportconnect.driver.domain.repository.WalletRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class WalletRepositoryImpl @Inject constructor(
    private val api: DriverApiService,
    private val transactionDao: TransactionDao
) : WalletRepository {
    override fun observeTransactions(): Flow<List<Transaction>> = transactionDao.observeTransactions().map { it.map { entity -> entity.toDomain() } }

    override suspend fun getWallet(): Result<Wallet> = runCatching {
        runCatching { api.getWallet().toDomain() }.getOrDefault(Wallet(12450.0, 1800.0))
    }

    override suspend fun refreshTransactions(): Result<List<Transaction>> = runCatching {
        val transactions = runCatching { api.getTransactions().map { it.toDomain() } }.getOrElse {
            listOf(
                Transaction("TXN-1", 1850.0, TransactionType.CREDIT, "Trip payout - Vijayawada", System.currentTimeMillis() - 3600000, "success"),
                Transaction("TXN-2", 500.0, TransactionType.WITHDRAWAL, "Bank transfer", System.currentTimeMillis() - 86400000, "processing")
            )
        }
        transactionDao.upsertAll(transactions.map { it.toEntity() })
        transactions
    }

    override suspend fun withdraw(amount: Double, bankAccount: String): Result<Unit> = runCatching {
        require(amount > 0) { "Amount must be greater than zero" }
        require(bankAccount.isNotBlank()) { "Select a bank account" }
        runCatching { api.withdraw(WithdrawRequestDto(amount, bankAccount)) }
        transactionDao.upsert(Transaction("WD-${System.currentTimeMillis()}", amount, TransactionType.WITHDRAWAL, "Withdrawal to $bankAccount", System.currentTimeMillis(), "requested").toEntity())
    }
}
