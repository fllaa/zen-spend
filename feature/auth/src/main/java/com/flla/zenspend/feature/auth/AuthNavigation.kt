package com.flla.zenspend.feature.auth

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation

fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    onAuthenticated: () -> Unit,
) {
    navigation(
        startDestination = AuthRoutes.LOGIN,
        route = AuthRoutes.GRAPH,
    ) {
        composable(AuthRoutes.LOGIN) {
            LoginRoute(
                onLoggedIn = onAuthenticated,
                onRegisterClick = { navController.navigate(AuthRoutes.REGISTER) },
            )
        }
        composable(AuthRoutes.REGISTER) {
            RegisterRoute(
                onRegistered = onAuthenticated,
                onLoginClick = { navController.popBackStack() },
            )
        }
    }
}
