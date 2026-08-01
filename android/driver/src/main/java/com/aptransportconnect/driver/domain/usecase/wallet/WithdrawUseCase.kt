package com.aptransportconnect.driver.domain.usecase.wallet

import com.aptransportconnect.driver.domain.repository.WalletRepository
import javax.inject.Inject

class WithdrawUseCase @Inject constructor(
    private val walletRepository: WalletRepository
) {
    suspend operator fun invoke(amount: Double, bankAccount: String): Result<Unit> =
        walletRepository.withdraw(amount, bankAccount)
}
