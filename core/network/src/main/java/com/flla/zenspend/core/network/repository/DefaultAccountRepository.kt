package com.flla.zenspend.core.network.repository

import com.flla.zenspend.core.database.seed.FinanceSeedData
import com.flla.zenspend.core.database.source.AccountLocalDataSource
import com.flla.zenspend.core.domain.repository.AccountRepository
import com.flla.zenspend.core.model.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultAccountRepository
    @Inject
    constructor(
        private val localDataSource: AccountLocalDataSource,
    ) : AccountRepository {
        init {
            runBlocking { seedIfEmpty() }
        }

        override fun observeAccounts(): Flow<List<Account>> {
            runBlocking { seedIfEmpty() }
            return localDataSource.observeAccounts()
        }

        override suspend fun saveAccount(account: Account) {
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

        override suspend fun toggleAccountVisibility(accountId: String) {
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

        private suspend fun seedIfEmpty() {
            if (localDataSource.isEmpty()) {
                localDataSource.upsertAccounts(FinanceSeedData.accounts)
            }
        }
    }
