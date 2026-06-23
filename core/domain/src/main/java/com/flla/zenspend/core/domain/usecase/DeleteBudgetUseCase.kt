package com.flla.zenspend.core.domain.usecase

import com.flla.zenspend.core.domain.repository.BudgetRepository
import javax.inject.Inject

class DeleteBudgetUseCase
    @Inject
    constructor(
        private val budgetRepository: BudgetRepository,
    ) {
        suspend operator fun invoke(id: String) {
            budgetRepository.deleteBudget(id)
        }
    }
