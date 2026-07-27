package com.aptransportconnect.driver.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsScreen(onLoggedOut: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(topBar = { TopAppBar(title = { Text("Settings") }) }) { padding ->
        when (val uiState = state) {
            SettingsUiState.Loading -> CircularProgressIndicator()
            is SettingsUiState.Error -> Text(uiState.message)
            is SettingsUiState.Success -> androidx.compose.foundation.layout.Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Notifications")
                    Switch(checked = uiState.settings.notificationsEnabled, onCheckedChange = viewModel::setNotifications)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Dark Theme")
                    Switch(checked = uiState.settings.isDarkTheme, onCheckedChange = viewModel::setTheme)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Online")
                    Switch(checked = uiState.settings.isOnline, onCheckedChange = viewModel::setOnline)
                }
                Button(onClick = { viewModel.logout(onLoggedOut) }, modifier = Modifier.fillMaxWidth()) { Text("Logout") }
            }
        }
    }
}
