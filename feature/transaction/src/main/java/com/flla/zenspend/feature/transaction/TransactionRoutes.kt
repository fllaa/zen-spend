package com.flla.zenspend.feature.transaction

object TransactionRoutes {
    const val GRAPH = "transaction_graph"
    const val TRANSACTION_DETAILS = "transaction/details?categoryId={categoryId}&isIncome={isIncome}"

    fun detailsRoute(
        categoryId: String,
        isIncome: Boolean,
    ): String {
        return "transaction/details?categoryId=$categoryId&isIncome=$isIncome"
    }
}
