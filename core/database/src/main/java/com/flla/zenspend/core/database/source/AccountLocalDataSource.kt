package com.flla.zenspend.core.database.source

import com.flla.zenspend.core.database.dao.AccountDao
import com.flla.zenspend.core.database.entity.asEntity
import com.flla.zenspend.core.database.entity.asExternalModel
import com.flla.zenspend.core.model.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountLocalDataSource
    @Inject
    constructor(
        private val accountDao: AccountDao,
    ) {
        fun observeAccounts(): Flow<List<Account>> =
            accountDao.observeAccounts().map { list ->
                list.map { it.asExternalModel() }
            }

        suspend fun getAccounts(): List<Account> = accountDao.getAccounts().map { it.asExternalModel() }

        suspend fun isEmpty(): Boolean = accountDao.countAccounts() == 0

        suspend fun upsertAccount(account: Account) {
            accountDao.upsertAccount(account.asEntity())
        }

        suspend fun upsertAccounts(accounts: List<Account>) {
            accountDao.upsertAccounts(accounts.map { it.asEntity() })
        }

        suspend fun clear() {
            accountDao.deleteAll()
        }
    }
