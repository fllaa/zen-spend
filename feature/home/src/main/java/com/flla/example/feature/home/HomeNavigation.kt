package com.flla.example.feature.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

fun NavGraphBuilder.homeGraph(
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    navigation(
        startDestination = HomeRoutes.HOME,
        route = HomeRoutes.GRAPH,
    ) {
        composable(HomeRoutes.HOME) {
            HomeRoute(
                onProfileClick = onProfileClick,
                onSettingsClick = onSettingsClick,
            )
        }
    }
}
