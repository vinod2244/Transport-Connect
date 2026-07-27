package com.aptransportconnect.driver.presentation.earnings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aptransportconnect.driver.utils.toCurrency

@Composable
fun EarningsScreen(viewModel: EarningsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(topBar = { TopAppBar(title = { Text("Earnings") }) }) { padding ->
        when (val uiState = state) {
            EarningsUiState.Loading -> CircularProgressIndicator()
            is EarningsUiState.Error -> Text(uiState.message, modifier = Modifier.padding(24.dp))
            is EarningsUiState.Success -> Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("Today" to uiState.earnings.today, "This Week" to uiState.earnings.week, "This Month" to uiState.earnings.month).forEach { (title, amount) ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(title)
                            Text(amount.toCurrency())
                            LinearProgressIndicator(progress = { (amount / uiState.earnings.month.coerceAtLeast(1.0)).toFloat() }, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
                Text("Total trips: ${uiState.earnings.totalTrips}")
                Text("Average per trip: ${uiState.averagePerTrip.toCurrency()}")
            }
        }
    }
}
