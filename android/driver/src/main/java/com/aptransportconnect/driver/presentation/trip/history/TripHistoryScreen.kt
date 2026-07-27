package com.aptransportconnect.driver.presentation.trip.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
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
import com.aptransportconnect.driver.utils.displayName
import com.aptransportconnect.driver.utils.toCurrency
import com.aptransportconnect.driver.utils.toDateGroup

@Composable
fun TripHistoryScreen(viewModel: TripHistoryViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(topBar = { TopAppBar(title = { Text("Trip History") }) }) { padding ->
        when (val uiState = state) {
            TripHistoryUiState.Loading -> Column(modifier = Modifier.fillMaxSize().padding(padding), verticalArrangement = Arrangement.Center) { CircularProgressIndicator() }
            is TripHistoryUiState.Error -> Text(uiState.message, modifier = Modifier.padding(24.dp))
            is TripHistoryUiState.Success -> Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TripHistoryFilter.entries.forEach { filter ->
                        AssistChip(onClick = { viewModel.setFilter(filter) }, label = { Text(filter.name.lowercase().replaceFirstChar { it.titlecase() }) })
                    }
                }
                val grouped = uiState.trips.groupBy { it.requestedAt.toDateGroup() }.entries.toList()
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(grouped) { group ->
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(group.key, style = MaterialTheme.typography.titleMedium)
                            group.value.forEach { trip ->
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("${trip.pickupAddress} → ${trip.dropAddress}")
                                        Text(trip.status.displayName())
                                        Text((trip.actualFare ?: trip.estimatedFare).toCurrency())
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
