package com.flla.zenspend.core.network.repository

import com.flla.zenspend.core.database.source.BudgetLocalDataSource
import com.flla.zenspend.core.domain.repository.BudgetRepository
import com.flla.zenspend.core.model.Budget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultBudgetRepository
    @Inject
    constructor(
        private val localDataSource: BudgetLocalDataSource,
        private val financeLocalInitializer: FinanceLocalInitializer,
    ) : BudgetRepository {
        override fun observeBudgets(): Flow<List<Budget>> {
            return localDataSource.observeBudgets().onStart {
                financeLocalInitializer.ensureInitialized()
            }
        }

        override suspend fun saveBudget(budget: Budget) {
            financeLocalInitializer.ensureInitialized()
            localDataSource.upsertBudget(budget)
        }

        override suspend fun clearBudgets() {
            financeLocalInitializer.ensureInitialized()
            localDataSource.clear()
        }

        override suspend fun deleteBudget(id: String) {
            financeLocalInitializer.ensureInitialized()
            localDataSource.deleteBudget(id)
        }
    }
