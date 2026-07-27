package com.aptransportconnect.driver.presentation.navigation_map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline

@Composable
fun NavigationScreen(viewModel: NavigationViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(topBar = { TopAppBar(title = { Text("Navigation") }) }) { _ ->
        when (val uiState = state) {
            NavigationUiState.Loading -> CircularProgressIndicator()
            is NavigationUiState.Error -> Text(uiState.message)
            is NavigationUiState.Success -> GoogleMap(modifier = Modifier.fillMaxSize()) {
                Marker(state = MarkerState(position = uiState.route.first()), title = "Pickup")
                Marker(state = MarkerState(position = uiState.route.last()), title = "Destination")
                Polyline(points = uiState.route, color = Color.Blue)
            }
        }
    }
}
