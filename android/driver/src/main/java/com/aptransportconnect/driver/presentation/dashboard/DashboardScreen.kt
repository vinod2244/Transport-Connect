package com.aptransportconnect.driver.presentation.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aptransportconnect.driver.utils.displayName
import com.aptransportconnect.driver.utils.toCurrency

private data class DashboardAction(val title: String, val icon: ImageVector, val onClick: () -> Unit)

@Composable
fun DashboardScreen(
    onRequestsClick: () -> Unit,
    onCurrentTripClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onWalletClick: () -> Unit,
    onEarningsClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onDocumentsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val actions = listOf(
        DashboardAction("Trip Requests", Icons.Default.Map, onRequestsClick),
        DashboardAction("Current Trip", Icons.Default.Map, onCurrentTripClick),
        DashboardAction("Trip History", Icons.Default.History, onHistoryClick),
        DashboardAction("Wallet", Icons.Default.Wallet, onWalletClick),
        DashboardAction("Earnings", Icons.Default.AttachMoney, onEarningsClick),
        DashboardAction("Notifications", Icons.Default.Notifications, onNotificationsClick),
        DashboardAction("Documents", Icons.Default.Description, onDocumentsClick),
        DashboardAction("Profile", Icons.Default.AccountCircle, onProfileClick),
        DashboardAction("Settings", Icons.Default.Settings, onSettingsClick)
    )

    Scaffold(topBar = { TopAppBar(title = { Text("Driver Dashboard") }) }) { padding ->
        when (val uiState = state) {
            DashboardUiState.Loading -> Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) { CircularProgressIndicator() }
            is DashboardUiState.Error -> Text(uiState.message, modifier = Modifier.padding(24.dp))
            is DashboardUiState.Success -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(uiState.profile.name, style = MaterialTheme.typography.titleLarge)
                                Text(if (isOnline) "You are online" else "You are offline")
                            }
                            Switch(checked = isOnline, onCheckedChange = { viewModel.toggleAvailability() })
                        }
                    }
                }
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Pending trip requests: ${uiState.pendingRequests}", style = MaterialTheme.typography.titleMedium)
                            Text("Today's earnings: ${uiState.earnings.today.toCurrency()}")
                            Text("Rating: ${uiState.profile.rating} ⭐")
                            uiState.currentTrip?.let { Text("Current trip: ${it.status.displayName()}") }
                        }
                    }
                }
                items(actions) { action ->
                    Card(modifier = Modifier.fillMaxWidth().clickable { action.onClick() }) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(action.icon, contentDescription = action.title)
                            Text(action.title, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    }
}
