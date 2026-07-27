package com.aptransportconnect.driver.domain.usecase.wallet

import com.aptransportconnect.driver.domain.model.Wallet
import com.aptransportconnect.driver.domain.repository.WalletRepository
import javax.inject.Inject

class GetWalletUseCase @Inject constructor(
    private val walletRepository: WalletRepository
) {
    suspend operator fun invoke(): Result<Wallet> = walletRepository.getWallet()
}
