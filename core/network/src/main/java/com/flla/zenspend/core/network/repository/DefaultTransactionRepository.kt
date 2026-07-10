package com.flla.zenspend.core.network.repository

import com.flla.zenspend.core.database.source.AccountLocalDataSource
import com.flla.zenspend.core.database.source.CategoryLocalDataSource
import com.flla.zenspend.core.database.source.TransactionLocalDataSource
import com.flla.zenspend.core.domain.repository.TransactionRepository
import com.flla.zenspend.core.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultTransactionRepository
    @Inject
    constructor(
        private val accountLocalDataSource: AccountLocalDataSource,
        private val categoryLocalDataSource: CategoryLocalDataSource,
        private val localDataSource: TransactionLocalDataSource,
        private val financeLocalInitializer: FinanceLocalInitializer,
    ) : TransactionRepository {
        override fun observeTransactions(): Flow<List<Transaction>> {
            return localDataSource.observeTransactions().onStart {
                financeLocalInitializer.ensureInitialized()
            }
        }

        override suspend fun saveTransaction(transaction: Transaction) {
            financeLocalInitializer.ensureInitialized()
            val account = accountLocalDataSource.getAccounts().firstOrNull { it.id == transaction.accountId }
            val category = categoryLocalDataSource.getCategories().firstOrNull { it.id == transaction.categoryId }
            val normalizedTransaction =
                transaction.copy(
                    categoryName = category?.name ?: transaction.categoryName,
                    accountName = account?.name ?: transaction.accountName,
                    isIncome = category?.isIncome ?: transaction.isIncome,
                )
            localDataSource.upsertTransaction(normalizedTransaction)
        }

        override suspend fun clearTransactions() {
            financeLocalInitializer.ensureInitialized()
            localDataSource.clear()
        }
    }
