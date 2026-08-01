package com.aptransportconnect.driver.presentation.withdraw

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aptransportconnect.driver.utils.toCurrency

@Composable
fun WithdrawScreen(viewModel: WithdrawViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var amount by remember { mutableStateOf("") }
    var bankAccount by remember { mutableStateOf("HDFC XXXX 4567") }

    LaunchedEffect(state) {
        when (val current = state) {
            is WithdrawUiState.Success -> if (current.submitted) snackbarHostState.showSnackbar("Withdrawal request submitted")
            is WithdrawUiState.Error -> snackbarHostState.showSnackbar(current.message)
            else -> Unit
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Withdraw") }) }, snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        when (val uiState = state) {
            WithdrawUiState.Loading -> CircularProgressIndicator()
            is WithdrawUiState.Error -> Text(uiState.message, modifier = Modifier.padding(24.dp))
            is WithdrawUiState.Success -> Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Available Balance: ${uiState.wallet.balance.toCurrency()}")
                OutlinedTextField(value = amount, onValueChange = { amount = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Amount") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                OutlinedTextField(value = bankAccount, onValueChange = { bankAccount = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Bank Account") })
                Button(onClick = { viewModel.submit(amount.toDoubleOrNull() ?: 0.0, bankAccount) }, modifier = Modifier.fillMaxWidth()) { Text("Submit Withdrawal") }
            }
        }
    }
}
