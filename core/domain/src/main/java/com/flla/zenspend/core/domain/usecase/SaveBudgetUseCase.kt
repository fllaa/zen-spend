package com.flla.zenspend.core.domain.usecase

import com.flla.zenspend.core.domain.repository.BudgetRepository
import com.flla.zenspend.core.model.Budget
import javax.inject.Inject

class SaveBudgetUseCase
    @Inject
    constructor(
        private val budgetRepository: BudgetRepository,
    ) {
        suspend operator fun invoke(budget: Budget) {
            budgetRepository.saveBudget(budget)
        }
    }
