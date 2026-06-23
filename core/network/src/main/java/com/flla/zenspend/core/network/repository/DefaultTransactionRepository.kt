package com.flla.zenspend.core.network.repository

import com.flla.zenspend.core.database.seed.FinanceSeedData
import com.flla.zenspend.core.database.source.TransactionLocalDataSource
import com.flla.zenspend.core.domain.repository.TransactionRepository
import com.flla.zenspend.core.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultTransactionRepository
    @Inject
    constructor(
        private val localDataSource: TransactionLocalDataSource,
    ) : TransactionRepository {
        init {
            runBlocking { seedIfEmpty() }
        }

        override fun observeTransactions(): Flow<List<Transaction>> {
            runBlocking { seedIfEmpty() }
            return localDataSource.observeTransactions()
        }

        override suspend fun saveTransaction(transaction: Transaction) {
            localDataSource.upsertTransaction(transaction)
        }

        private suspend fun seedIfEmpty() {
            if (localDataSource.isEmpty()) {
                localDataSource.upsertTransactions(FinanceSeedData.transactions)
            }
        }
    }
