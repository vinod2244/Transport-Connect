package com.aptransportconnect.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.aptransportconnect.domain.model.PaymentState
import com.aptransportconnect.presentation.navigation.AppRoute
import com.aptransportconnect.presentation.viewmodel.AuthViewModel
import com.aptransportconnect.presentation.viewmodel.BookingDetailsViewModel
import com.aptransportconnect.presentation.viewmodel.BookingHistoryViewModel
import com.aptransportconnect.presentation.viewmodel.BookingViewModel
import com.aptransportconnect.presentation.viewmodel.ChatViewModel
import com.aptransportconnect.presentation.viewmodel.NotificationsViewModel
import com.aptransportconnect.presentation.viewmodel.PaymentViewModel
import com.aptransportconnect.presentation.viewmodel.ProfileViewModel
import com.aptransportconnect.presentation.viewmodel.SearchVehicleViewModel
import com.aptransportconnect.presentation.viewmodel.SettingsViewModel
import com.aptransportconnect.presentation.viewmodel.SplashViewModel
import com.aptransportconnect.presentation.viewmodel.TrackingViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun SplashScreen(vm: SplashViewModel, navigate: (String) -> Unit) {
    val state by vm.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.nextRoute) {
        if (!state.isLoading && state.nextRoute.isNotBlank()) navigate(state.nextRoute)
    }
    CenterText(if (state.isLoading) "Loading..." else "Redirecting...")
}

@Composable
fun OnboardingScreen(onContinue: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("Welcome to Transport Connect")
        Spacer(Modifier.height(12.dp))
        Text("Book vehicles, track rides, pay securely, and chat with drivers.")
        Spacer(Modifier.height(24.dp))
        Button(onClick = onContinue) { Text("Get Started") }
    }
}

@Composable
fun LoginScreen(vm: AuthViewModel, onOtp: (String) -> Unit, onSuccess: () -> Unit) {
    val state by vm.state.collectAsStateWithLifecycle()
    var mobile by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }

    LaunchedEffect(state.loggedIn) { if (state.loggedIn) onSuccess() }

    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("Login")
        OutlinedTextField(value = mobile, onValueChange = { mobile = it }, label = { Text("Mobile") })
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") })
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { rememberMe = !rememberMe }) { Text(if (rememberMe) "Remember Me: ON" else "Remember Me: OFF") }
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { vm.login(mobile, password, rememberMe) }) { Text("Password Login") }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { onOtp(mobile) }) { Text("OTP Login") }
        state.error?.let { Text(it) }
        if (state.loading) CircularProgressIndicator()
    }
}

@Composable
fun OtpScreen(vm: AuthViewModel, mobile: String, onVerified: () -> Unit) {
    val state by vm.state.collectAsStateWithLifecycle()
    var otp by remember { mutableStateOf("") }
    LaunchedEffect(state.loggedIn) { if (state.loggedIn) onVerified() }

    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("OTP Verification for $mobile")
        OutlinedTextField(value = otp, onValueChange = { otp = it }, label = { Text("OTP") })
        Button(onClick = { vm.verifyOtp(mobile, otp, rememberMe = true) }) { Text("Verify OTP") }
        if (state.loading) CircularProgressIndicator()
    }
}

@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    val destinations = listOf(
        "Search Vehicle" to "search",
        "Booking History" to "booking_history",
        "Notifications" to "notifications",
        "Chat" to "chat/${AppRoute.DEFAULT_CHAT_THREAD}",
        "Profile" to "profile",
        "Settings" to "settings",
    )
    LazyColumn(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { Text("Home") }
        items(destinations) { (label, route) -> Button(onClick = { onNavigate(route) }) { Text(label) } }
    }
}

@Composable
fun SearchVehicleScreen(vm: SearchVehicleViewModel, onBook: (String) -> Unit) {
    val vehicles by vm.vehicles.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Search Vehicle")
        OutlinedTextField(value = query, onValueChange = {
            query = it
            vm.search(it)
        }, label = { Text("Search") })
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(vehicles) { vehicle ->
                Card(Modifier.fillMaxWidth().padding(4.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        AsyncImage(model = vehicle.imageUrl, contentDescription = vehicle.name)
                        Text(vehicle.name)
                        Text("${vehicle.type} • ${vehicle.capacityTons}T")
                        Button(onClick = { onBook(vehicle.id) }) { Text("Book") }
                    }
                }
            }
        }
    }
}

@Composable
fun BookingScreen(vm: BookingViewModel, vehicleId: String, onCreated: (String) -> Unit) {
    val booking by vm.current.collectAsStateWithLifecycle()
    var pickup by remember { mutableStateOf("") }
    var drop by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("2500") }
    var etaText by remember { mutableStateOf("120") }

    LaunchedEffect(booking?.id) { booking?.id?.let(onCreated) }

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Booking")
        Text("Vehicle: $vehicleId")
        OutlinedTextField(value = pickup, onValueChange = { pickup = it }, label = { Text("Pickup") })
        OutlinedTextField(value = drop, onValueChange = { drop = it }, label = { Text("Drop") })
        OutlinedTextField(value = amountText, onValueChange = { amountText = it }, label = { Text("Amount") })
        OutlinedTextField(value = etaText, onValueChange = { etaText = it }, label = { Text("ETA (minutes)") })
        Button(onClick = {
            vm.create(
                vehicleId = vehicleId,
                pickup = pickup,
                drop = drop,
                amount = amountText.toDoubleOrNull() ?: 0.0,
                etaMinutes = etaText.toIntOrNull() ?: 0,
            )
        }) { Text("Create Booking") }
    }
}

@Composable
fun BookingDetailsScreen(vm: BookingDetailsViewModel, bookingId: String, onTrack: () -> Unit, onPay: (Double) -> Unit) {
    val state by vm.state.collectAsStateWithLifecycle()
    LaunchedEffect(bookingId) { vm.load(bookingId) }
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Booking Details")
        Text("Booking ID: $bookingId")
        Text("Status: ${state?.status ?: "Loading"}")
        Text("Amount: ₹${state?.amount ?: 0.0}")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onTrack) { Text("Live Tracking") }
            Button(onClick = { onPay(state?.amount ?: 0.0) }) { Text("Pay") }
        }
    }
}

@Composable
fun LiveTrackingScreen(vm: TrackingViewModel, bookingId: String) {
    val tracking by vm.tracking.collectAsStateWithLifecycle()
    LaunchedEffect(bookingId) { vm.load(bookingId) }

    val location = tracking?.let { LatLng(it.lat, it.lng) } ?: LatLng(17.3850, 78.4867)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 13f)
    }

    Column(Modifier.fillMaxSize()) {
        Text("Live Tracking", modifier = Modifier.padding(16.dp))
        GoogleMap(
            modifier = Modifier.fillMaxWidth().weight(1f),
            cameraPositionState = cameraPositionState,
        ) {
            Marker(state = MarkerState(position = location), title = tracking?.driverName ?: "Driver")
        }
    }
}

@Composable
fun PaymentScreen(vm: PaymentViewModel, bookingId: String, amount: Double) {
    val result by vm.result.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Payment")
        Text("Booking: $bookingId")
        Text("Amount: ₹$amount")
        Button(onClick = { vm.pay(bookingId, amount) }) { Text("Pay now") }
        when (result?.state) {
            PaymentState.INITIATED -> Text("Payment initiated")
            PaymentState.SUCCESS -> Text("Payment success: ${result?.transactionId.orEmpty()}")
            PaymentState.FAILURE -> Text("Payment failed")
            null -> Unit
        }
    }
}

@Composable
fun BookingHistoryScreen(vm: BookingHistoryViewModel, onOpen: (String) -> Unit) {
    val items by vm.items.collectAsStateWithLifecycle()
    LazyColumn(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { Text("Booking History") }
        items(items) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text("${it.pickup} → ${it.drop}")
                    Text(it.status)
                    Button(onClick = { onOpen(it.id) }) { Text("Details") }
                }
            }
        }
    }
}

@Composable
fun NotificationsScreen(vm: NotificationsViewModel) {
    val items by vm.items.collectAsStateWithLifecycle()
    LazyColumn(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { Text("Notifications") }
        items(items) {
            Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(12.dp)) { Text(it.title); Text(it.message) } }
        }
    }
}

@Composable
fun ChatScreen(vm: ChatViewModel) {
    val messages by vm.messages.collectAsStateWithLifecycle()
    var text by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Chat")
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(messages) { Text("${it.sender}: ${it.text}") }
        }
        OutlinedTextField(value = text, onValueChange = { text = it }, label = { Text("Message") })
        Button(onClick = {
            vm.send(text)
            text = ""
        }) { Text("Send") }
    }
}

@Composable
fun ProfileScreen(vm: ProfileViewModel) {
    val profile by vm.profile.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Profile")
        AsyncImage(model = profile?.avatarUrl, contentDescription = "Avatar")
        Text("Name: ${profile?.name.orEmpty()}")
        Text("Mobile: ${profile?.mobile.orEmpty()}")
        Text("Email: ${profile?.email.orEmpty()}")
    }
}

@Composable
fun SettingsScreen(vm: SettingsViewModel, onLogout: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Settings")
        Button(onClick = {
            vm.logout()
            onLogout()
        }) { Text("Logout") }
    }
}

@Composable
private fun CenterText(text: String) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(text)
    }
}
