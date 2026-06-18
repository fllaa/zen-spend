package com.flla.zenspend.feature.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

fun NavGraphBuilder.homeGraph() {
    navigation(
        startDestination = HomeRoutes.HOME,
        route = HomeRoutes.GRAPH,
    ) {
        composable(HomeRoutes.HOME) {
            HomeRoute()
        }
    }
}
