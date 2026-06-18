package com.flla.zenspend.feature.setup

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

fun NavGraphBuilder.setupGraph(onCompleted: () -> Unit) {
    navigation(
        startDestination = SetupRoutes.SETUP,
        route = SetupRoutes.GRAPH,
    ) {
        composable(SetupRoutes.SETUP) {
            SetupRoute(
                onCompleted = onCompleted,
            )
        }
    }
}
