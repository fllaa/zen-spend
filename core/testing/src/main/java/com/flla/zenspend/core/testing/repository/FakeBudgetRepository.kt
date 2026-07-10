package com.flla.zenspend.core.testing.repository

import com.flla.zenspend.core.domain.repository.BudgetRepository
import com.flla.zenspend.core.model.Budget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeBudgetRepository : BudgetRepository {
    private val budgetsFlow = MutableStateFlow<List<Budget>>(emptyList())

    override fun observeBudgets(): Flow<List<Budget>> = budgetsFlow

    override suspend fun saveBudget(budget: Budget) {
        budgetsFlow.update { currentList ->
            val existingIndex = currentList.indexOfFirst { it.id == budget.id || it.categoryId == budget.categoryId }
            if (existingIndex >= 0) {
                currentList.toMutableList().apply {
                    set(existingIndex, budget)
                }
            } else {
                currentList + budget
            }
        }
    }

    override suspend fun clearBudgets() {
        budgetsFlow.value = emptyList()
    }

    override suspend fun deleteBudget(id: String) {
        budgetsFlow.update { currentList ->
            currentList.filterNot { it.id == id }
        }
    }

    fun setBudgets(budgets: List<Budget>) {
        budgetsFlow.value = budgets
    }
}
