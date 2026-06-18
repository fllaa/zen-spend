package com.flla.zenspend.feature.history

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

fun NavGraphBuilder.historyGraph() {
    navigation(
        startDestination = HistoryRoutes.HISTORY,
        route = HistoryRoutes.GRAPH,
    ) {
        composable(HistoryRoutes.HISTORY) {
            HistoryRoute()
        }
    }
}
