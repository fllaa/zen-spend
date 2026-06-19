package com.flla.zenspend.core.domain.usecase

import com.flla.zenspend.core.domain.repository.AccountRepository
import javax.inject.Inject

class ToggleAccountVisibilityUseCase
    @Inject
    constructor(
        private val accountRepository: AccountRepository,
    ) {
        suspend operator fun invoke(accountId: String) {
            accountRepository.toggleAccountVisibility(accountId)
        }
    }
