package com.flla.example

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
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

    LaunchedEffect(sessionState) {
        val target = when (sessionState) {
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

    NavHost(
        navController = navController,
        startDestination = ExampleRoutes.SPLASH,
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
        homeGraph(
            onProfileClick = { navController.navigate(ProfileRoutes.PROFILE) },
            onSettingsClick = { navController.navigate(SettingsRoutes.SETTINGS) },
        )
        profileScreen(onBackClick = { navController.popBackStack() })
        settingsScreen(onBackClick = { navController.popBackStack() })
    }
}
