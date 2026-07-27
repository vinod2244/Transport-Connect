package com.aptransportconnect.driver.presentation.wallet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aptransportconnect.driver.domain.model.TransactionType
import com.aptransportconnect.driver.utils.toCurrency

@Composable
fun WalletScreen(onWithdrawClick: () -> Unit, viewModel: WalletViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(topBar = { TopAppBar(title = { Text("Wallet") }) }) { padding ->
        when (val uiState = state) {
            WalletUiState.Loading -> CircularProgressIndicator()
            is WalletUiState.Error -> Text(uiState.message, modifier = Modifier.padding(24.dp))
            is WalletUiState.Success -> Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Available Balance", style = MaterialTheme.typography.titleMedium)
                        Text(uiState.wallet.balance.toCurrency(), style = MaterialTheme.typography.headlineMedium)
                        Text("Pending payout: ${uiState.wallet.pendingPayout.toCurrency()}")
                    }
                }
                Button(onClick = onWithdrawClick, modifier = Modifier.fillMaxWidth()) { Text("Withdraw Funds") }
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.transactions) { transaction ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(transaction.description)
                                Text(if (transaction.type == TransactionType.CREDIT) "+${transaction.amount.toCurrency()}" else "-${transaction.amount.toCurrency()}")
                                Text(transaction.status)
                            }
                        }
                    }
                }
            }
        }
    }
}
