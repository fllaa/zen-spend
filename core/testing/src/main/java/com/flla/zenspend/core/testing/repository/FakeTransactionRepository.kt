package com.flla.zenspend.core.testing.repository

import com.flla.zenspend.core.domain.repository.TransactionRepository
import com.flla.zenspend.core.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeTransactionRepository : TransactionRepository {
    private val transactionsFlow = MutableStateFlow<List<Transaction>>(emptyList())

    override fun observeTransactions(): Flow<List<Transaction>> = transactionsFlow

    override suspend fun saveTransaction(transaction: Transaction) {
        transactionsFlow.update { listOf(transaction) + it }
    }

    override suspend fun clearTransactions() {
        transactionsFlow.value = emptyList()
    }

    fun setTransactions(transactions: List<Transaction>) {
        transactionsFlow.value = transactions
    }
}
