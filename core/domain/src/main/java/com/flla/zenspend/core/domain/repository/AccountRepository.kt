package com.flla.zenspend.core.domain.repository

import com.flla.zenspend.core.model.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun observeAccounts(): Flow<List<Account>>

    suspend fun saveAccount(account: Account)

    suspend fun clearAccounts()

    suspend fun toggleAccountVisibility(accountId: String)
}
