package com.flla.zenspend.core.model

data class Budget(
    val id: String,
    val categoryId: String,
    val limitAmount: Long,
    val period: BudgetPeriod = BudgetPeriod.MONTHLY,
)

enum class BudgetPeriod {
    WEEKLY,
    MONTHLY,
}
