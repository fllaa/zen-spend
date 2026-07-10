package com.flla.zenspend.core.network.repository

import com.flla.zenspend.core.database.source.AccountLocalDataSource
import com.flla.zenspend.core.domain.repository.AccountRepository
import com.flla.zenspend.core.model.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultAccountRepository
    @Inject
    constructor(
        private val localDataSource: AccountLocalDataSource,
        private val financeLocalInitializer: FinanceLocalInitializer,
    ) : AccountRepository {
        override fun observeAccounts(): Flow<List<Account>> {
            return localDataSource.observeAccounts().onStart {
                financeLocalInitializer.ensureInitialized()
            }
        }

        override suspend fun saveAccount(account: Account) {
            financeLocalInitializer.ensureInitialized()
            val existingAccounts = localDataSource.getAccounts()
            val normalizedAccounts =
                if (account.isPrimary) {
                    existingAccounts.map { it.copy(isPrimary = false) }
                } else {
                    existingAccounts
                }
            localDataSource.upsertAccounts(normalizedAccounts)
            localDataSource.upsertAccount(account)
        }

        override suspend fun clearAccounts() {
            financeLocalInitializer.ensureInitialized()
            localDataSource.clear()
        }

        override suspend fun toggleAccountVisibility(accountId: String) {
            financeLocalInitializer.ensureInitialized()
            val updatedAccounts =
                localDataSource.getAccounts().map { account ->
                    if (account.id == accountId) {
                        account.copy(isVisible = !account.isVisible)
                    } else {
                        account
                    }
                }
            localDataSource.upsertAccounts(updatedAccounts)
        }
    }
