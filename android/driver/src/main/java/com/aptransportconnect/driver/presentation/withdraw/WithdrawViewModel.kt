package com.aptransportconnect.driver.presentation.withdraw

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aptransportconnect.driver.domain.model.Wallet
import com.aptransportconnect.driver.domain.usecase.wallet.GetWalletUseCase
import com.aptransportconnect.driver.domain.usecase.wallet.WithdrawUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface WithdrawUiState {
    data object Loading : WithdrawUiState
    data class Success(val wallet: Wallet, val submitted: Boolean = false) : WithdrawUiState
    data class Error(val message: String) : WithdrawUiState
}

@HiltViewModel
class WithdrawViewModel @Inject constructor(
    private val getWalletUseCase: GetWalletUseCase,
    private val withdrawUseCase: WithdrawUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<WithdrawUiState>(WithdrawUiState.Loading)
    val uiState: StateFlow<WithdrawUiState> = _uiState.asStateFlow()

    init { loadBalance() }

    fun loadBalance() {
        viewModelScope.launch {
            _uiState.value = WithdrawUiState.Loading
            getWalletUseCase()
                .onSuccess { _uiState.value = WithdrawUiState.Success(it) }
                .onFailure { _uiState.value = WithdrawUiState.Error(it.message ?: "Unable to load balance") }
        }
    }

    fun submit(amount: Double, bankAccount: String) {
        viewModelScope.launch {
            withdrawUseCase(amount, bankAccount)
                .onSuccess {
                    val wallet = (_uiState.value as? WithdrawUiState.Success)?.wallet ?: Wallet(0.0, 0.0)
                    _uiState.value = WithdrawUiState.Success(wallet, submitted = true)
                }
                .onFailure { _uiState.value = WithdrawUiState.Error(it.message ?: "Withdrawal request failed") }
        }
    }
}
