package com.flla.zenspend

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.flla.zenspend.core.model.SessionState
import com.flla.zenspend.feature.auth.AuthRoutes
import com.flla.zenspend.feature.auth.authGraph
import com.flla.zenspend.feature.home.HomeRoutes
import com.flla.zenspend.feature.home.homeGraph
import com.flla.zenspend.feature.onboarding.OnboardingRoutes
import com.flla.zenspend.feature.onboarding.onboardingGraph
import com.flla.zenspend.feature.profile.ProfileRoutes
import com.flla.zenspend.feature.profile.profileScreen
import com.flla.zenspend.feature.settings.SettingsRoutes
import com.flla.zenspend.feature.settings.settingsScreen
import com.flla.zenspend.navigation.SplashScreen
import com.flla.zenspend.navigation.ZenSpendRoutes

private val mainDestinations =
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

@Composable
fun ZenSpendApp(
    sessionState: SessionState,
    hasCompletedOnboarding: Boolean,
) {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = mainDestinations.any { it.selectedRoute == currentRoute }

    SessionNavigationEffect(
        navController = navController,
        sessionState = sessionState,
        hasCompletedOnboarding = hasCompletedOnboarding,
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                MainBottomBar(
                    destinations = mainDestinations,
                    currentRoute = currentRoute,
                    onDestinationClick = navController::navigateToMainDestination,
                )
            }
        },
    ) { padding ->
        AppNavHost(
            navController = navController,
            startDestination = ZenSpendRoutes.SPLASH,
            modifier = Modifier.padding(padding),
            sessionState = sessionState,
        )
    }
}

@Composable
private fun SessionNavigationEffect(
    navController: NavHostController,
    sessionState: SessionState,
    hasCompletedOnboarding: Boolean,
) {
    val minSplashDuration = 2000L
    val startTime = remember { System.currentTimeMillis() }
    LaunchedEffect(sessionState, hasCompletedOnboarding) {
        if (sessionState == SessionState.Loading) return@LaunchedEffect

        val elapsed = System.currentTimeMillis() - startTime
        val remainingDelay = minSplashDuration - elapsed
        if (remainingDelay > 0) {
            kotlinx.coroutines.delay(remainingDelay)
        }

        val target =
            when (sessionState) {
                SessionState.Loading -> return@LaunchedEffect
                SessionState.Authenticated -> HomeRoutes.GRAPH
                SessionState.Unauthenticated,
                SessionState.Expired,
                -> {
                    if (hasCompletedOnboarding) AuthRoutes.GRAPH else OnboardingRoutes.GRAPH
                }
            }
        navController.navigate(target) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }
}

@Composable
private fun AppNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    sessionState: SessionState,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(ZenSpendRoutes.SPLASH) {
            SplashScreen(sessionExpired = sessionState == SessionState.Expired)
        }
        onboardingGraph(
            navController = navController,
            onCompleted = {
                navController.navigate(AuthRoutes.GRAPH) {
                    popUpTo(OnboardingRoutes.GRAPH) { inclusive = true }
                    launchSingleTop = true
                }
            },
        )
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

@Composable
private fun MainBottomBar(
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
