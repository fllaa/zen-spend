package com.flla.zenspend.core.domain.repository

import com.flla.zenspend.core.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun observeTransactions(): Flow<List<Transaction>>

    suspend fun saveTransaction(transaction: Transaction)
}
