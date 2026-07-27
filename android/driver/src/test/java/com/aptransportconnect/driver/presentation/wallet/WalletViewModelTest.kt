package com.aptransportconnect.driver.presentation.wallet

import com.aptransportconnect.driver.MainDispatcherRule
import com.aptransportconnect.driver.domain.model.Transaction
import com.aptransportconnect.driver.domain.model.TransactionType
import com.aptransportconnect.driver.domain.model.Wallet
import com.aptransportconnect.driver.domain.repository.WalletRepository
import com.aptransportconnect.driver.domain.usecase.wallet.GetTransactionsUseCase
import com.aptransportconnect.driver.domain.usecase.wallet.GetWalletUseCase
import com.aptransportconnect.driver.domain.usecase.wallet.WithdrawUseCase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WalletViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun loadWallet_emitsBalance() = runTest {
        val repository = FakeWalletRepository()
        val viewModel = WalletViewModel(GetWalletUseCase(repository), GetTransactionsUseCase(repository), WithdrawUseCase(repository))
        advanceUntilIdle()
        val state = viewModel.uiState.value as WalletUiState.Success
        assertThat(state.wallet.balance).isEqualTo(1000.0)
    }

    @Test
    fun withdraw_invalidAmount_emitsError() = runTest {
        val repository = FakeWalletRepository(failOnWithdraw = true)
        val viewModel = WalletViewModel(GetWalletUseCase(repository), GetTransactionsUseCase(repository), WithdrawUseCase(repository))
        advanceUntilIdle()
        viewModel.withdraw(0.0, "bank")
        advanceUntilIdle()
        assertThat(viewModel.uiState.value).isInstanceOf(WalletUiState.Error::class.java)
    }
}

private class FakeWalletRepository(private val failOnWithdraw: Boolean = false) : WalletRepository {
    override fun observeTransactions(): Flow<List<Transaction>> = flowOf(listOf(Transaction("1", 100.0, TransactionType.CREDIT, "Trip", 0, "success")))
    override suspend fun getWallet(): Result<Wallet> = Result.success(Wallet(1000.0, 100.0))
    override suspend fun refreshTransactions(): Result<List<Transaction>> = Result.success(listOf(Transaction("1", 100.0, TransactionType.CREDIT, "Trip", 0, "success")))
    override suspend fun withdraw(amount: Double, bankAccount: String): Result<Unit> = if (failOnWithdraw || amount <= 0) Result.failure(IllegalArgumentException("Invalid amount")) else Result.success(Unit)
}
