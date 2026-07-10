package com.flla.zenspend.core.testing.repository

import com.flla.zenspend.core.domain.repository.AccountRepository
import com.flla.zenspend.core.model.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeAccountRepository : AccountRepository {
    private val accountsFlow = MutableStateFlow<List<Account>>(emptyList())

    override fun observeAccounts(): Flow<List<Account>> = accountsFlow

    override suspend fun saveAccount(account: Account) {
        accountsFlow.update { currentList ->
            val listWithNewAccount =
                if (account.isPrimary) {
                    currentList.map { it.copy(isPrimary = false) } + account
                } else {
                    currentList + account
                }
            listWithNewAccount
        }
    }

    override suspend fun clearAccounts() {
        accountsFlow.value = emptyList()
    }

    override suspend fun toggleAccountVisibility(accountId: String) {
        accountsFlow.update { currentList ->
            currentList.map { account ->
                if (account.id == accountId) {
                    account.copy(isVisible = !account.isVisible)
                } else {
                    account
                }
            }
        }
    }

    fun setAccounts(accounts: List<Account>) {
        accountsFlow.value = accounts
    }
}
