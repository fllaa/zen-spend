package com.flla.zenspend.core.database.source

import com.flla.zenspend.core.database.dao.BudgetDao
import com.flla.zenspend.core.database.entity.asEntity
import com.flla.zenspend.core.database.entity.asExternalModel
import com.flla.zenspend.core.model.Budget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetLocalDataSource
    @Inject
    constructor(
        private val budgetDao: BudgetDao,
    ) {
        fun observeBudgets(): Flow<List<Budget>> =
            budgetDao.observeBudgets().map { list ->
                list.map { it.asExternalModel() }
            }

        suspend fun isEmpty(): Boolean = budgetDao.countBudgets() == 0

        suspend fun upsertBudget(budget: Budget) {
            budgetDao.upsertBudget(budget.asEntity())
        }

        suspend fun upsertBudgets(budgets: List<Budget>) {
            budgetDao.upsertBudgets(budgets.map { it.asEntity() })
        }

        suspend fun deleteBudget(id: String) {
            budgetDao.deleteBudget(id)
        }

        suspend fun clear() {
            budgetDao.deleteAll()
        }
    }
