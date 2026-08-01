package com.aptransportconnect.driver.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(topBar = { TopAppBar(title = { Text("Profile") }) }) { padding ->
        when (val uiState = state) {
            ProfileUiState.Loading -> CircularProgressIndicator()
            is ProfileUiState.Error -> Text(uiState.message, modifier = Modifier.padding(24.dp))
            is ProfileUiState.Success -> {
                var name by remember(uiState.profile.id) { mutableStateOf(uiState.profile.name) }
                var email by remember(uiState.profile.id) { mutableStateOf(uiState.profile.email) }
                Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    AsyncImage(model = uiState.profile.profilePhotoUrl, contentDescription = "Profile photo", modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = name, onValueChange = { name = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Name") })
                    OutlinedTextField(value = email, onValueChange = { email = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Email") })
                    Text("Vehicle: ${uiState.profile.vehicleNumber} • ${uiState.profile.vehicleType}")
                    Text("Rating: ${uiState.profile.rating} ⭐")
                    Button(onClick = { viewModel.saveProfile(uiState.profile.copy(name = name, email = email)) }, modifier = Modifier.fillMaxWidth()) { Text("Save Profile") }
                }
            }
        }
    }
}
