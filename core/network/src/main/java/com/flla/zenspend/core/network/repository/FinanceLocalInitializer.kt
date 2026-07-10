package com.flla.zenspend.core.network.repository

import com.flla.zenspend.core.database.seed.FinanceSeedData
import com.flla.zenspend.core.database.source.AccountLocalDataSource
import com.flla.zenspend.core.database.source.BudgetLocalDataSource
import com.flla.zenspend.core.database.source.CategoryLocalDataSource
import com.flla.zenspend.core.database.source.TransactionLocalDataSource
import com.flla.zenspend.core.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinanceLocalInitializer
    @Inject
    constructor(
        private val accountLocalDataSource: AccountLocalDataSource,
        private val categoryLocalDataSource: CategoryLocalDataSource,
        private val budgetLocalDataSource: BudgetLocalDataSource,
        private val transactionLocalDataSource: TransactionLocalDataSource,
        private val userPreferencesRepository: UserPreferencesRepository,
    ) {
        private val initializationMutex = Mutex()
        @Volatile
        private var hasCheckedInitialization = false

        suspend fun ensureInitialized() {
            if (hasCheckedInitialization) return

            initializationMutex.withLock {
                if (hasCheckedInitialization) return

                val hasCompletedSetup = userPreferencesRepository.preferences.first().hasCompletedSetup
                val isFreshInstall =
                    accountLocalDataSource.isEmpty() &&
                        categoryLocalDataSource.isEmpty() &&
                        budgetLocalDataSource.isEmpty() &&
                        transactionLocalDataSource.isEmpty()

                if (!hasCompletedSetup && isFreshInstall) {
                    accountLocalDataSource.upsertAccounts(FinanceSeedData.accounts)
                    categoryLocalDataSource.upsertCategories(FinanceSeedData.categories)
                    budgetLocalDataSource.upsertBudgets(FinanceSeedData.budgets)
                    transactionLocalDataSource.upsertTransactions(FinanceSeedData.transactions)
                }

                hasCheckedInitialization = true
            }
        }
    }
