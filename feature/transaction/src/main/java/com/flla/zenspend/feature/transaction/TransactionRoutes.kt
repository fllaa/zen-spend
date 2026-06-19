package com.flla.zenspend.feature.transaction

object TransactionRoutes {
    const val GRAPH = "transaction_graph"
    const val TRANSACTION_DETAILS = "transaction/details?category={category}&isIncome={isIncome}"

    fun detailsRoute(category: String, isIncome: Boolean): String {
        return "transaction/details?category=$category&isIncome=$isIncome"
    }
}
