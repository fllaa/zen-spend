package com.flla.zenspend.feature.analytics

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

fun NavGraphBuilder.analyticsGraph(
    onNavigateToCategoryDetail: () -> Unit,
    onBackClick: () -> Unit,
) {
    navigation(
        startDestination = AnalyticsRoutes.ANALYTICS,
        route = AnalyticsRoutes.GRAPH,
    ) {
        composable(AnalyticsRoutes.ANALYTICS) {
            AnalyticsRoute(onNavigateToCategoryDetail = onNavigateToCategoryDetail)
        }
        composable(AnalyticsRoutes.CATEGORY_DETAIL) {
            AnalyticsCategoryDetailRoute(onBackClick = onBackClick)
        }
    }
}
