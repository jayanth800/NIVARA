package com.nivara.app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nivara.app.ui.auth.AuthScreen
import com.nivara.app.ui.home.HomeScreen
import com.nivara.app.ui.pricing.PricingScreen
import com.nivara.app.ui.profile.AiAssistantScreen
import com.nivara.app.ui.profile.ProfileScreen
import com.nivara.app.ui.share.ListMyItemScreen
import com.nivara.app.ui.share.OwnerDashboardScreen
import com.nivara.app.ui.share.ShareMarketplaceScreen
import com.nivara.app.ui.splash.OnboardingScreen
import com.nivara.app.ui.splash.SplashScreen
import com.nivara.app.ui.storage.MyStorageScreen
import com.nivara.app.ui.storage.RetrieveItemScreen
import com.nivara.app.ui.storage.StoreItemScreen
import com.nivara.app.ui.tracking.OrderTrackingScreen
import com.nivara.app.ui.theme.ChampagneGold
import com.nivara.app.ui.theme.DeepForestGreen
import com.nivara.app.ui.theme.WarmIvory

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object MyStorage : Screen("mystorage", "Storage", Icons.Default.Inventory2)
    object Share : Screen("share", "Share", Icons.Default.Storefront)
    object Track : Screen("tracking", "Track", Icons.Default.LocalShipping)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
}

@Composable
fun NivaraRootNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavScreens = listOf(
        Screen.Home,
        Screen.MyStorage,
        Screen.Share,
        Screen.Track,
        Screen.Profile
    )

    val showBottomBar = currentRoute in bottomNavScreens.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = DeepForestGreen,
                    contentColor = WarmIvory
                ) {
                    bottomNavScreens.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DeepForestGreen,
                                unselectedIconColor = WarmIvory,
                                selectedTextColor = ChampagneGold,
                                unselectedTextColor = WarmIvory.copy(alpha = 0.7f),
                                indicatorColor = ChampagneGold
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("splash") {
                SplashScreen(onSplashFinished = {
                    navController.navigate("onboarding") {
                        popUpTo("splash") { inclusive = true }
                    }
                })
            }
            composable("onboarding") {
                OnboardingScreen(onFinished = {
                    navController.navigate("auth") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                })
            }
            composable("auth") {
                AuthScreen(onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo("auth") { inclusive = true }
                    }
                })
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToStore = { navController.navigate("storeitem") },
                    onNavigateToRetrieve = { navController.navigate("retrieveitem") },
                    onNavigateToMyStorage = { navController.navigate(Screen.MyStorage.route) },
                    onNavigateToShare = { navController.navigate(Screen.Share.route) },
                    onNavigateToPricing = { navController.navigate("pricing") },
                    onNavigateToTracking = { navController.navigate(Screen.Track.route) },
                    onNavigateToAssistant = { navController.navigate("aiassistant") }
                )
            }
            composable(Screen.MyStorage.route) {
                MyStorageScreen(
                    onNavigateToStore = { navController.navigate("storeitem") },
                    onNavigateToRetrieve = { navController.navigate("retrieveitem") },
                    onBoxClick = { box ->
                        // navigate to detail or pricing
                    }
                )
            }
            composable(Screen.Share.route) {
                ShareMarketplaceScreen(
                    onNavigateToListMyItem = { navController.navigate("listmyitem") },
                    onNavigateToOwnerDashboard = { navController.navigate("ownerdashboard") },
                    onItemClick = { item ->
                        // navigate to rental detail
                    }
                )
            }
            composable(Screen.Track.route) {
                OrderTrackingScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen(onLogout = {
                    navController.navigate("auth") {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                })
            }
            composable("storeitem") {
                StoreItemScreen(onBookingConfirmed = {
                    navController.navigate(Screen.Track.route) {
                        popUpTo("storeitem") { inclusive = true }
                    }
                })
            }
            composable("retrieveitem") {
                RetrieveItemScreen(onRetrievalConfirmed = {
                    navController.navigate(Screen.Track.route) {
                        popUpTo("retrieveitem") { inclusive = true }
                    }
                })
            }
            composable("listmyitem") {
                ListMyItemScreen(onItemListed = {
                    navController.navigate(Screen.Share.route) {
                        popUpTo("listmyitem") { inclusive = true }
                    }
                })
            }
            composable("ownerdashboard") {
                OwnerDashboardScreen()
            }
            composable("pricing") {
                PricingScreen(onNavigateToStore = { navController.navigate("storeitem") })
            }
            composable("aiassistant") {
                AiAssistantScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
