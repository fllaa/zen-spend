package com.flla.zenspend.core.domain.usecase

import com.flla.zenspend.core.domain.repository.BudgetRepository
import javax.inject.Inject

class ObserveBudgetsUseCase
    @Inject
    constructor(
        private val budgetRepository: BudgetRepository,
    ) {
        operator fun invoke() = budgetRepository.observeBudgets()
    }
