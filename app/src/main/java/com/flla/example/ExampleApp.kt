package com.flla.example

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.flla.example.core.model.SessionState
import com.flla.example.feature.auth.AuthRoutes
import com.flla.example.feature.auth.authGraph
import com.flla.example.feature.home.HomeRoutes
import com.flla.example.feature.home.homeGraph
import com.flla.example.feature.profile.ProfileRoutes
import com.flla.example.feature.profile.profileScreen
import com.flla.example.feature.settings.SettingsRoutes
import com.flla.example.feature.settings.settingsScreen
import com.flla.example.navigation.ExampleRoutes
import com.flla.example.navigation.SplashScreen

@Composable
fun ExampleApp(sessionState: SessionState) {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = navBackStackEntry?.destination?.route
    val mainDestinations =
        listOf(
            MainDestination(
                route = HomeRoutes.GRAPH,
                selectedRoute = HomeRoutes.HOME,
                label = "Home",
                icon = Icons.Filled.Home,
            ),
            MainDestination(
                route = ProfileRoutes.PROFILE,
                selectedRoute = ProfileRoutes.PROFILE,
                label = "Profile",
                icon = Icons.Filled.Person,
            ),
            MainDestination(
                route = SettingsRoutes.SETTINGS,
                selectedRoute = SettingsRoutes.SETTINGS,
                label = "Settings",
                icon = Icons.Filled.Settings,
            ),
        )
    val showBottomBar = mainDestinations.any { it.selectedRoute == currentRoute }

    LaunchedEffect(sessionState) {
        val target =
            when (sessionState) {
                SessionState.Loading -> return@LaunchedEffect
                SessionState.Authenticated -> HomeRoutes.GRAPH
                SessionState.Unauthenticated,
                SessionState.Expired,
                -> AuthRoutes.GRAPH
            }
        navController.navigate(target) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                mainBottomBar(
                    destinations = mainDestinations,
                    currentRoute = currentRoute,
                    onDestinationClick = navController::navigateToMainDestination,
                )
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = ExampleRoutes.SPLASH,
            modifier = Modifier.padding(padding),
        ) {
            composable(ExampleRoutes.SPLASH) {
                SplashScreen(sessionExpired = sessionState == SessionState.Expired)
            }
            authGraph(
                navController = navController,
                onAuthenticated = {
                    navController.navigate(HomeRoutes.GRAPH) {
                        popUpTo(AuthRoutes.GRAPH) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
            homeGraph()
            profileScreen()
            settingsScreen()
        }
    }
}

@Composable
private fun mainBottomBar(
    destinations: List<MainDestination>,
    currentRoute: String?,
    onDestinationClick: (MainDestination) -> Unit,
) {
    NavigationBar {
        destinations.forEach { destination ->
            val selected = currentRoute == destination.selectedRoute
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        onDestinationClick(destination)
                    }
                },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.label,
                    )
                },
                label = { Text(destination.label) },
            )
        }
    }
}

private fun NavHostController.navigateToMainDestination(destination: MainDestination) {
    navigate(destination.route) {
        popUpTo(HomeRoutes.GRAPH) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

private data class MainDestination(
    val route: String,
    val selectedRoute: String,
    val label: String,
    val icon: ImageVector,
)
