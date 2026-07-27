package com.aptransportconnect.driver.domain.usecase.wallet

import com.aptransportconnect.driver.domain.model.Transaction
import com.aptransportconnect.driver.domain.repository.WalletRepository
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val walletRepository: WalletRepository
) {
    suspend operator fun invoke(): Result<List<Transaction>> = walletRepository.refreshTransactions()
}
