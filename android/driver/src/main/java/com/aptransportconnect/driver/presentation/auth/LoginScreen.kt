package com.aptransportconnect.driver.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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

@Composable
fun LoginScreen(onAuthenticated: () -> Unit, viewModel: AuthViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var phone by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        when (val current = state) {
            is AuthUiState.CodeSent -> {
                otpSent = true
                snackbarHostState.showSnackbar(current.message)
            }
            AuthUiState.Authenticated -> onAuthenticated()
            is AuthUiState.Error -> snackbarHostState.showSnackbar(current.message)
            else -> Unit
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Driver Login") }) }, snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Sign in using your phone number and OTP.", style = MaterialTheme.typography.bodyLarge)
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Phone Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            if (otpSent) {
                OutlinedTextField(
                    value = otp,
                    onValueChange = { otp = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Enter OTP") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            when (state) {
                AuthUiState.Loading -> CircularProgressIndicator()
                else -> {
                    Button(
                        onClick = { if (otpSent) viewModel.verifyOtp(phone, otp) else viewModel.requestOtp(phone) },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 14.dp)
                    ) { Text(if (otpSent) "Verify OTP" else "Send OTP") }
                    if (otpSent) {
                        Button(onClick = { viewModel.refreshSession() }, modifier = Modifier.fillMaxWidth()) { Text("Refresh Session") }
                    }
                }
            }
        }
    }
}
