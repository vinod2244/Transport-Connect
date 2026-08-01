package com.aptransportconnect.driver.presentation.notifications

import androidx.compose.foundation.clickable
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
import com.aptransportconnect.driver.utils.toDisplayDate

@Composable
fun NotificationsScreen(viewModel: NotificationsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(topBar = { TopAppBar(title = { Text("Notifications") }) }) { padding ->
        when (val uiState = state) {
            NotificationsUiState.Loading -> CircularProgressIndicator()
            is NotificationsUiState.Error -> Text(uiState.message, modifier = Modifier.padding(24.dp))
            is NotificationsUiState.Success -> Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { viewModel.markAllRead() }, modifier = Modifier.fillMaxWidth()) { Text("Mark All Read") }
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.notifications) { notification ->
                        Card(modifier = Modifier.fillMaxWidth().clickable { viewModel.markRead(notification.id) }) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(notification.title, style = if (notification.isRead) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge)
                                Text(notification.body)
                                Text(notification.timestamp.toDisplayDate())
                            }
                        }
                    }
                }
            }
        }
    }
}
