package com.flla.zenspend.core.domain.repository

import com.flla.zenspend.core.model.Budget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun observeBudgets(): Flow<List<Budget>>

    suspend fun saveBudget(budget: Budget)

    suspend fun clearBudgets()

    suspend fun deleteBudget(id: String)
}
