package com.flla.zenspend.feature.transaction

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation

fun NavController.navigateToTransactionDetails(category: String, isIncome: Boolean) {
    navigate(TransactionRoutes.detailsRoute(category, isIncome))
}

fun NavGraphBuilder.transactionGraph(
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
) {
    navigation(
        startDestination = TransactionRoutes.TRANSACTION_DETAILS,
        route = TransactionRoutes.GRAPH,
    ) {
        composable(
            route = TransactionRoutes.TRANSACTION_DETAILS,
            arguments = listOf(
                navArgument("category") {
                    type = NavType.StringType
                    defaultValue = "Lainnya"
                },
                navArgument("isIncome") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: "Lainnya"
            val isIncome = backStackEntry.arguments?.getBoolean("isIncome") ?: false

            TransactionRoute(
                category = category,
                isIncome = isIncome,
                onBackClick = onBackClick,
                onSaveSuccess = onSaveSuccess,
            )
        }
    }
}
