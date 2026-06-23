package com.flla.zenspend.core.network.repository

import com.flla.zenspend.core.database.seed.FinanceSeedData
import com.flla.zenspend.core.database.source.BudgetLocalDataSource
import com.flla.zenspend.core.domain.repository.BudgetRepository
import com.flla.zenspend.core.model.Budget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultBudgetRepository
    @Inject
    constructor(
        private val localDataSource: BudgetLocalDataSource,
    ) : BudgetRepository {
        init {
            runBlocking { seedIfEmpty() }
        }

        override fun observeBudgets(): Flow<List<Budget>> {
            runBlocking { seedIfEmpty() }
            return localDataSource.observeBudgets()
        }

        override suspend fun saveBudget(budget: Budget) {
            localDataSource.upsertBudget(budget)
        }

        override suspend fun deleteBudget(id: String) {
            localDataSource.deleteBudget(id)
        }

        private suspend fun seedIfEmpty() {
            if (localDataSource.isEmpty()) {
                localDataSource.upsertBudgets(FinanceSeedData.budgets)
            }
        }
    }
