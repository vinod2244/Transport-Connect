package com.aptransportconnect.driver.presentation.documents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
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

@Composable
fun DocumentsScreen(viewModel: DocumentsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(topBar = { TopAppBar(title = { Text("Documents") }) }) { padding ->
        when (val uiState = state) {
            DocumentsUiState.Loading -> CircularProgressIndicator()
            is DocumentsUiState.Error -> Text(uiState.message, modifier = Modifier.padding(24.dp))
            is DocumentsUiState.Success -> LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.documents) { document ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(document.name)
                            AssistChip(onClick = {}, label = { Text(document.status.name) })
                            Button(onClick = { viewModel.uploadPlaceholder(document.type) }, modifier = Modifier.fillMaxWidth()) { Text("Upload / Replace") }
                        }
                    }
                }
            }
        }
    }
}
