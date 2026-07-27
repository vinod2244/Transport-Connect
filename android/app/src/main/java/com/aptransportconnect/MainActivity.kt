package com.aptransportconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.aptransportconnect.presentation.navigation.AppNavGraph
import com.aptransportconnect.presentation.theme.TransportConnectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TransportConnectTheme {
                AppNavGraph()
            }
        }
    }
}
