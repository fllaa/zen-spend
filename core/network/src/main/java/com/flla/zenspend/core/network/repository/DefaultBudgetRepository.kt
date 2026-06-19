package com.flla.zenspend.core.network.repository

import com.flla.zenspend.core.domain.repository.BudgetRepository
import com.flla.zenspend.core.model.Budget
import com.flla.zenspend.core.model.BudgetPeriod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultBudgetRepository
    @Inject
    constructor() : BudgetRepository {
        private val budgetsFlow: MutableStateFlow<List<Budget>>

        init {
            val initialBudgets =
                listOf(
                    Budget(
                        id = "b_makan",
                        categoryId = "cat_makan",
                        limitAmount = 3000000L,
                        period = BudgetPeriod.MONTHLY,
                    ),
                    Budget(
                        id = "b_transport",
                        categoryId = "cat_transport",
                        limitAmount = 2000000L,
                        period = BudgetPeriod.MONTHLY,
                    ),
                    Budget(
                        id = "b_hiburan",
                        categoryId = "cat_hiburan",
                        limitAmount = 1500000L,
                        period = BudgetPeriod.MONTHLY,
                    ),
                    Budget(
                        id = "b_belanja",
                        categoryId = "cat_belanja",
                        limitAmount = 2000000L,
                        period = BudgetPeriod.MONTHLY,
                    ),
                )
            budgetsFlow = MutableStateFlow(initialBudgets)
        }

        override fun observeBudgets(): Flow<List<Budget>> = budgetsFlow

        override suspend fun saveBudget(budget: Budget) {
            budgetsFlow.update { currentList ->
                val existingIndex =
                    currentList.indexOfFirst {
                        it.id == budget.id || it.categoryId == budget.categoryId
                    }
                if (existingIndex >= 0) {
                    currentList.toMutableList().apply {
                        set(existingIndex, budget)
                    }
                } else {
                    currentList + budget
                }
            }
        }

        override suspend fun deleteBudget(id: String) {
            budgetsFlow.update { currentList ->
                currentList.filterNot { it.id == id }
            }
        }
    }
