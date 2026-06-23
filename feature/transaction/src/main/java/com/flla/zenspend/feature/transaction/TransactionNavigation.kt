package com.flla.zenspend.feature.transaction

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation

fun NavController.navigateToTransactionDetails(
    categoryId: String,
    isIncome: Boolean,
) {
    navigate(TransactionRoutes.detailsRoute(categoryId, isIncome))
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
            arguments =
                listOf(
                    navArgument("categoryId") {
                        type = NavType.StringType
                        defaultValue = "cat_lainnya_expense"
                    },
                    navArgument("isIncome") {
                        type = NavType.BoolType
                        defaultValue = false
                    },
                ),
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: "cat_lainnya_expense"
            val isIncome = backStackEntry.arguments?.getBoolean("isIncome") ?: false

            TransactionRoute(
                categoryId = categoryId,
                isIncome = isIncome,
                onBackClick = onBackClick,
                onSaveSuccess = onSaveSuccess,
            )
        }
    }
}
