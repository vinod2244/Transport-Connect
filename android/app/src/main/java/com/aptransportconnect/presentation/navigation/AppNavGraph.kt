package com.aptransportconnect.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aptransportconnect.presentation.screen.BookingDetailsScreen
import com.aptransportconnect.presentation.screen.BookingHistoryScreen
import com.aptransportconnect.presentation.screen.BookingScreen
import com.aptransportconnect.presentation.screen.ChatScreen
import com.aptransportconnect.presentation.screen.HomeScreen
import com.aptransportconnect.presentation.screen.LiveTrackingScreen
import com.aptransportconnect.presentation.screen.LoginScreen
import com.aptransportconnect.presentation.screen.NotificationsScreen
import com.aptransportconnect.presentation.screen.OnboardingScreen
import com.aptransportconnect.presentation.screen.OtpScreen
import com.aptransportconnect.presentation.screen.PaymentScreen
import com.aptransportconnect.presentation.screen.ProfileScreen
import com.aptransportconnect.presentation.screen.SearchVehicleScreen
import com.aptransportconnect.presentation.screen.SettingsScreen
import com.aptransportconnect.presentation.screen.SplashScreen
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
import com.aptransportconnect.presentation.viewmodel.vmFactory
import com.aptransportconnect.utils.ServiceLocator
import kotlinx.coroutines.launch

object AppRoute {
    const val DEFAULT_CHAT_THREAD = "support-thread"
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val OTP = "otp"
    const val HOME = "home"
    const val SEARCH = "search"
    const val BOOKING = "booking"
    const val BOOKING_DETAILS = "booking_details/{bookingId}"
    const val TRACKING = "tracking/{bookingId}"
    const val PAYMENT = "payment/{bookingId}/{amount}"
    const val BOOKING_HISTORY = "booking_history"
    const val NOTIFICATIONS = "notifications"
    const val CHAT = "chat/{threadId}"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val repository = ServiceLocator.repository(context)

    NavHost(navController = navController, startDestination = AppRoute.SPLASH) {
        composable(AppRoute.SPLASH) {
            val vm: SplashViewModel = viewModel(factory = vmFactory { SplashViewModel(repository) })
            SplashScreen(vm) { navController.navigate(it) { popUpTo(AppRoute.SPLASH) { inclusive = true } } }
        }
        composable(AppRoute.ONBOARDING) {
            val scope = rememberCoroutineScope()
            OnboardingScreen(onContinue = {
                scope.launch {
                    repository.setOnboardingDone(true)
                    navController.navigate(AppRoute.LOGIN) { popUpTo(AppRoute.ONBOARDING) { inclusive = true } }
                }
            })
        }
        composable(AppRoute.LOGIN) {
            val vm: AuthViewModel = viewModel(factory = vmFactory { AuthViewModel(repository) })
            LoginScreen(vm, onOtp = { mobile ->
                navController.currentBackStackEntry?.savedStateHandle?.set("mobile", mobile)
                navController.navigate(AppRoute.OTP)
            }, onSuccess = { navController.navigate(AppRoute.HOME) { popUpTo(AppRoute.LOGIN) { inclusive = true } } })
        }
        composable(AppRoute.OTP) {
            val vm: AuthViewModel = viewModel(factory = vmFactory { AuthViewModel(repository) })
            val mobile = navController.previousBackStackEntry?.savedStateHandle?.get<String>("mobile") ?: ""
            OtpScreen(vm, mobile = mobile, onVerified = {
                navController.navigate(AppRoute.HOME) { popUpTo(AppRoute.LOGIN) { inclusive = true } }
            })
        }
        composable(AppRoute.HOME) { HomeScreen(onNavigate = { navController.navigate(it) }) }
        composable(AppRoute.SEARCH) {
            val vm: SearchVehicleViewModel = viewModel(factory = vmFactory { SearchVehicleViewModel(repository) })
            SearchVehicleScreen(vm) { vehicleId ->
                navController.currentBackStackEntry?.savedStateHandle?.set("vehicleId", vehicleId)
                navController.navigate(AppRoute.BOOKING)
            }
        }
        composable(AppRoute.BOOKING) {
            val vm: BookingViewModel = viewModel(factory = vmFactory { BookingViewModel(repository) })
            BookingScreen(vm, vehicleId = navController.previousBackStackEntry?.savedStateHandle?.get<String>("vehicleId") ?: "") { bookingId ->
                navController.navigate("booking_details/$bookingId")
            }
        }
        composable(
            route = AppRoute.BOOKING_DETAILS,
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType }),
        ) { entry ->
            val vm: BookingDetailsViewModel = viewModel(factory = vmFactory { BookingDetailsViewModel(repository) })
            val bookingId = entry.arguments?.getString("bookingId").orEmpty()
            BookingDetailsScreen(vm, bookingId,
                onTrack = { navController.navigate("tracking/$bookingId") },
                onPay = { amount -> navController.navigate("payment/$bookingId/$amount") })
        }
        composable(
            route = AppRoute.TRACKING,
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType }),
        ) { entry ->
            val vm: TrackingViewModel = viewModel(factory = vmFactory { TrackingViewModel(repository) })
            LiveTrackingScreen(vm, entry.arguments?.getString("bookingId").orEmpty())
        }
        composable(
            route = AppRoute.PAYMENT,
            arguments = listOf(
                navArgument("bookingId") { type = NavType.StringType },
                navArgument("amount") { type = NavType.FloatType },
            ),
        ) { entry ->
            val vm: PaymentViewModel = viewModel(factory = vmFactory { PaymentViewModel(repository) })
            PaymentScreen(
                vm,
                bookingId = entry.arguments?.getString("bookingId").orEmpty(),
                amount = entry.arguments?.getFloat("amount")?.toDouble() ?: 0.0,
            )
        }
        composable(AppRoute.BOOKING_HISTORY) {
            val vm: BookingHistoryViewModel = viewModel(factory = vmFactory { BookingHistoryViewModel(repository) })
            BookingHistoryScreen(vm, onOpen = { navController.navigate("booking_details/$it") })
        }
        composable(AppRoute.NOTIFICATIONS) {
            val vm: NotificationsViewModel = viewModel(factory = vmFactory { NotificationsViewModel(repository) })
            NotificationsScreen(vm)
        }
        composable(
            route = AppRoute.CHAT,
            arguments = listOf(navArgument("threadId") { type = NavType.StringType }),
        ) { entry ->
            val threadId = entry.arguments?.getString("threadId").orEmpty().ifBlank { AppRoute.DEFAULT_CHAT_THREAD }
            val vm: ChatViewModel = viewModel(factory = vmFactory { ChatViewModel(repository, threadId) })
            ChatScreen(vm)
        }
        composable(AppRoute.PROFILE) {
            val vm: ProfileViewModel = viewModel(factory = vmFactory { ProfileViewModel(repository) })
            ProfileScreen(vm)
        }
        composable(AppRoute.SETTINGS) {
            val vm: SettingsViewModel = viewModel(factory = vmFactory { SettingsViewModel(repository) })
            SettingsScreen(vm, onLogout = {
                navController.navigate(AppRoute.LOGIN) { popUpTo(AppRoute.HOME) { inclusive = true } }
            })
        }
    }
}
