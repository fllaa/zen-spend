package com.flla.zenspend.core.domain.usecase

import com.flla.zenspend.core.domain.repository.AccountRepository
import com.flla.zenspend.core.model.Account
import javax.inject.Inject

class SaveAccountUseCase
    @Inject
    constructor(
        private val accountRepository: AccountRepository,
    ) {
        suspend operator fun invoke(account: Account) {
            accountRepository.saveAccount(account)
        }
    }
