package com.aptransportconnect.driver.presentation.trip.requests

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aptransportconnect.driver.utils.toCurrency

@Composable
fun TripRequestsScreen(onTripSelected: (String) -> Unit, viewModel: TripRequestsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(topBar = { TopAppBar(title = { Text("Trip Requests") }) }) { padding ->
        when (val uiState = state) {
            TripRequestsUiState.Loading -> Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) { CircularProgressIndicator() }
            is TripRequestsUiState.Error -> Text(uiState.message, modifier = Modifier.padding(24.dp))
            is TripRequestsUiState.Success -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.requests) { item ->
                    Card(modifier = Modifier.fillMaxWidth().clickable { onTripSelected(item.trip.id) }) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(item.trip.customerName, style = MaterialTheme.typography.titleMedium)
                            Text("Pickup: ${item.trip.pickupAddress}")
                            Text("Drop: ${item.trip.dropAddress}")
                            Text("Fare: ${item.trip.estimatedFare.toCurrency()} • ${item.trip.distance} km")
                            Text("Accept within ${item.secondsRemaining}s", color = Color(0xFFB45309))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Button(onClick = { viewModel.acceptTrip(item.trip.id) }, modifier = Modifier.weight(1f)) { Text("Accept") }
                                OutlinedButton(onClick = { viewModel.rejectTrip(item.trip.id) }, modifier = Modifier.weight(1f)) { Text("Reject") }
                            }
                        }
                    }
                }
            }
        }
    }
}
