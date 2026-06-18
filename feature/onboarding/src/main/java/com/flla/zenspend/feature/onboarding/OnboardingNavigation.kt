package com.flla.zenspend.feature.onboarding

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

fun NavGraphBuilder.onboardingGraph(onCompleted: () -> Unit) {
    navigation(
        startDestination = OnboardingRoutes.ONBOARDING,
        route = OnboardingRoutes.GRAPH,
    ) {
        composable(OnboardingRoutes.ONBOARDING) {
            OnboardingRoute(
                onCompleted = onCompleted,
            )
        }
    }
}
