package com.flla.zenspend.core.domain.usecase

import com.flla.zenspend.core.domain.repository.AccountRepository
import com.flla.zenspend.core.model.Account
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAccountsUseCase
    @Inject
    constructor(
        private val accountRepository: AccountRepository,
    ) {
        operator fun invoke(): Flow<List<Account>> {
            return accountRepository.observeAccounts()
        }
    }
