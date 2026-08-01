package com.aptransportconnect.driver.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aptransportconnect.driver.data.local.datastore.DriverPreferences
import com.aptransportconnect.driver.presentation.auth.LoginScreen
import com.aptransportconnect.driver.presentation.dashboard.DashboardScreen
import com.aptransportconnect.driver.presentation.documents.DocumentsScreen
import com.aptransportconnect.driver.presentation.earnings.EarningsScreen
import com.aptransportconnect.driver.presentation.navigation_map.NavigationScreen
import com.aptransportconnect.driver.presentation.notifications.NotificationsScreen
import com.aptransportconnect.driver.presentation.profile.ProfileScreen
import com.aptransportconnect.driver.presentation.settings.SettingsScreen
import com.aptransportconnect.driver.presentation.splash.SplashScreen
import com.aptransportconnect.driver.presentation.trip.current.CurrentTripScreen
import com.aptransportconnect.driver.presentation.trip.details.TripDetailsScreen
import com.aptransportconnect.driver.presentation.trip.history.TripHistoryScreen
import com.aptransportconnect.driver.presentation.trip.requests.TripRequestsScreen
import com.aptransportconnect.driver.presentation.wallet.WalletScreen
import com.aptransportconnect.driver.presentation.withdraw.WithdrawScreen
import com.aptransportconnect.driver.utils.Constants

@Composable
fun DriverNavGraph(preferences: DriverPreferences) {
    val navController = rememberNavController()
    val isLoggedIn by preferences.isLoggedIn.collectAsStateWithLifecycle(initialValue = false)

    NavHost(navController = navController, startDestination = Constants.ROUTE_SPLASH) {
        composable(Constants.ROUTE_SPLASH) {
            SplashScreen(isLoggedIn = isLoggedIn) { loggedIn ->
                navController.navigate(if (loggedIn) Constants.ROUTE_DASHBOARD else Constants.ROUTE_LOGIN) {
                    popUpTo(Constants.ROUTE_SPLASH) { inclusive = true }
                }
            }
        }
        composable(Constants.ROUTE_LOGIN) {
            LoginScreen {
                navController.navigate(Constants.ROUTE_DASHBOARD) {
                    popUpTo(Constants.ROUTE_LOGIN) { inclusive = true }
                }
            }
        }
        composable(Constants.ROUTE_DASHBOARD) {
            DashboardScreen(
                onRequestsClick = { navController.navigate(Constants.ROUTE_TRIP_REQUESTS) },
                onCurrentTripClick = { navController.navigate(Constants.ROUTE_CURRENT_TRIP) },
                onHistoryClick = { navController.navigate(Constants.ROUTE_TRIP_HISTORY) },
                onWalletClick = { navController.navigate(Constants.ROUTE_WALLET) },
                onEarningsClick = { navController.navigate(Constants.ROUTE_EARNINGS) },
                onNotificationsClick = { navController.navigate(Constants.ROUTE_NOTIFICATIONS) },
                onDocumentsClick = { navController.navigate(Constants.ROUTE_DOCUMENTS) },
                onProfileClick = { navController.navigate(Constants.ROUTE_PROFILE) },
                onSettingsClick = { navController.navigate(Constants.ROUTE_SETTINGS) }
            )
        }
        composable(Constants.ROUTE_TRIP_REQUESTS) {
            TripRequestsScreen(onTripSelected = { navController.navigate("${Constants.ROUTE_TRIP_DETAILS}/$it") })
        }
        composable(
            route = "${Constants.ROUTE_TRIP_DETAILS}/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.StringType })
        ) { backStackEntry ->
            TripDetailsScreen(tripId = backStackEntry.arguments?.getString("tripId").orEmpty())
        }
        composable(Constants.ROUTE_CURRENT_TRIP) { CurrentTripScreen(onNavigateClick = { navController.navigate(Constants.ROUTE_NAVIGATION) }) }
        composable(Constants.ROUTE_TRIP_HISTORY) { TripHistoryScreen() }
        composable(Constants.ROUTE_NAVIGATION) { NavigationScreen() }
        composable(Constants.ROUTE_WALLET) { WalletScreen(onWithdrawClick = { navController.navigate(Constants.ROUTE_WITHDRAW) }) }
        composable(Constants.ROUTE_EARNINGS) { EarningsScreen() }
        composable(Constants.ROUTE_WITHDRAW) { WithdrawScreen() }
        composable(Constants.ROUTE_NOTIFICATIONS) { NotificationsScreen() }
        composable(Constants.ROUTE_DOCUMENTS) { DocumentsScreen() }
        composable(Constants.ROUTE_PROFILE) { ProfileScreen() }
        composable(Constants.ROUTE_SETTINGS) {
            SettingsScreen {
                navController.navigate(Constants.ROUTE_LOGIN) {
                    popUpTo(Constants.ROUTE_DASHBOARD) { inclusive = true }
                }
            }
        }
    }
}
