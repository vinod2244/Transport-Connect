package com.aptransportconnect.driver.presentation.trip.current

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

@Composable
fun CurrentTripScreen(onNavigateClick: () -> Unit, viewModel: CurrentTripViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(topBar = { TopAppBar(title = { Text("Current Trip") }) }) { padding ->
        when (val uiState = state) {
            CurrentTripUiState.Loading -> Column(modifier = Modifier.fillMaxSize().padding(padding), verticalArrangement = Arrangement.Center) { CircularProgressIndicator() }
            is CurrentTripUiState.Error -> Text(uiState.message, modifier = Modifier.padding(24.dp))
            is CurrentTripUiState.Success -> {
                val trip = uiState.trip
                Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Card(modifier = Modifier.fillMaxWidth().height(280.dp)) {
                        GoogleMap(modifier = Modifier.fillMaxSize()) {
                            Marker(state = MarkerState(LatLng(trip.pickupLat, trip.pickupLng)), title = "Pickup")
                            Marker(state = MarkerState(LatLng(trip.dropLat, trip.dropLng)), title = "Destination")
                        }
                    }
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("${trip.customerName} • ${trip.status.displayName()}")
                            Text("Pickup: ${trip.pickupAddress}")
                            Text("Drop: ${trip.dropAddress}")
                        }
                    }
                    Button(onClick = onNavigateClick, modifier = Modifier.fillMaxWidth()) { Text("Open Navigation") }
                    Button(onClick = { viewModel.advanceStatus() }, modifier = Modifier.fillMaxWidth()) { Text(viewModel.actionLabel(trip.status)) }
                }
            }
        }
    }
}
