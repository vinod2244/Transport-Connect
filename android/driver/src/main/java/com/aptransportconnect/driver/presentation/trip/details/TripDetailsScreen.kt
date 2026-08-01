package com.aptransportconnect.driver.presentation.trip.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aptransportconnect.driver.utils.displayName
import com.aptransportconnect.driver.utils.toCurrency
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

@Composable
fun TripDetailsScreen(tripId: String, viewModel: TripDetailsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(tripId) { viewModel.loadTrip(tripId) }
    Scaffold(topBar = { TopAppBar(title = { Text("Trip Details") }) }) { padding ->
        when (val uiState = state) {
            TripDetailsUiState.Loading -> Column(modifier = Modifier.fillMaxSize().padding(padding), verticalArrangement = Arrangement.Center) { CircularProgressIndicator() }
            is TripDetailsUiState.Error -> Text(uiState.message, modifier = Modifier.padding(24.dp))
            is TripDetailsUiState.Success -> {
                val trip = uiState.trip
                Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Card(modifier = Modifier.fillMaxWidth().height(220.dp)) {
                        GoogleMap(modifier = Modifier.fillMaxSize()) {
                            Marker(state = MarkerState(LatLng(trip.pickupLat, trip.pickupLng)), title = "Pickup")
                            Marker(state = MarkerState(LatLng(trip.dropLat, trip.dropLng)), title = "Drop")
                        }
                    }
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(trip.customerName, style = MaterialTheme.typography.titleLarge)
                            Text(trip.customerPhone)
                            Text("Pickup: ${trip.pickupAddress}")
                            Text("Drop: ${trip.dropAddress}")
                            Text("Estimated fare: ${trip.estimatedFare.toCurrency()}")
                            Text("Vehicle: ${trip.vehicleType}")
                            Text("Status: ${trip.status.displayName()}")
                        }
                    }
                }
            }
        }
    }
}
