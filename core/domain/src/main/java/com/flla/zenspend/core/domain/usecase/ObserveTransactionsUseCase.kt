package com.flla.zenspend.core.domain.usecase

import com.flla.zenspend.core.domain.repository.TransactionRepository
import com.flla.zenspend.core.model.Transaction
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTransactionsUseCase
    @Inject
    constructor(
        private val transactionRepository: TransactionRepository,
    ) {
        operator fun invoke(): Flow<List<Transaction>> {
            return transactionRepository.observeTransactions()
        }
    }
