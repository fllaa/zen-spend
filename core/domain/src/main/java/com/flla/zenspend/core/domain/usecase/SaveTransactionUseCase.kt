package com.flla.zenspend.core.domain.usecase

import com.flla.zenspend.core.domain.repository.TransactionRepository
import com.flla.zenspend.core.model.Transaction
import javax.inject.Inject

class SaveTransactionUseCase
    @Inject
    constructor(
        private val transactionRepository: TransactionRepository,
    ) {
        suspend operator fun invoke(transaction: Transaction) {
            transactionRepository.saveTransaction(transaction)
        }
    }
