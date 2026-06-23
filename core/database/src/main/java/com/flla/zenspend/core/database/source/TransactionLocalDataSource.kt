package com.flla.zenspend.core.database.source

import com.flla.zenspend.core.database.dao.TransactionDao
import com.flla.zenspend.core.database.entity.asEntity
import com.flla.zenspend.core.database.entity.asExternalModel
import com.flla.zenspend.core.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionLocalDataSource
    @Inject
    constructor(
        private val transactionDao: TransactionDao,
    ) {
        fun observeTransactions(): Flow<List<Transaction>> =
            transactionDao.observeTransactions().map { list -> list.map { it.asExternalModel() } }

        suspend fun isEmpty(): Boolean = transactionDao.countTransactions() == 0

        suspend fun upsertTransaction(transaction: Transaction) {
            transactionDao.upsertTransaction(transaction.asEntity())
        }

        suspend fun upsertTransactions(transactions: List<Transaction>) {
            transactionDao.upsertTransactions(transactions.map { it.asEntity() })
        }

        suspend fun clear() {
            transactionDao.deleteAll()
        }
    }
