package com.aptransportconnect.driver.presentation.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aptransportconnect.driver.domain.model.Transaction
import com.aptransportconnect.driver.domain.model.Wallet
import com.aptransportconnect.driver.domain.usecase.wallet.GetTransactionsUseCase
import com.aptransportconnect.driver.domain.usecase.wallet.GetWalletUseCase
import com.aptransportconnect.driver.domain.usecase.wallet.WithdrawUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface WalletUiState {
    data object Loading : WalletUiState
    data class Success(val wallet: Wallet, val transactions: List<Transaction>) : WalletUiState
    data class Error(val message: String) : WalletUiState
}

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val getWalletUseCase: GetWalletUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val withdrawUseCase: WithdrawUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<WalletUiState>(WalletUiState.Loading)
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

    init { loadWallet() }

    fun loadWallet() {
        viewModelScope.launch {
            _uiState.value = WalletUiState.Loading
            val wallet = getWalletUseCase().getOrElse { Wallet(0.0, 0.0) }
            val transactions = getTransactionsUseCase().getOrElse { emptyList() }
            _uiState.value = WalletUiState.Success(wallet, transactions)
        }
    }

    fun withdraw(amount: Double, bankAccount: String) {
        viewModelScope.launch {
            withdrawUseCase(amount, bankAccount)
                .onSuccess { loadWallet() }
                .onFailure { _uiState.value = WalletUiState.Error(it.message ?: "Withdrawal failed") }
        }
    }
}
