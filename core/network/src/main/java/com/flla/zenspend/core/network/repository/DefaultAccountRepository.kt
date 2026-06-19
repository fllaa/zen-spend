package com.flla.zenspend.core.network.repository

import com.flla.zenspend.core.domain.repository.AccountRepository
import com.flla.zenspend.core.model.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultAccountRepository
    @Inject
    constructor() : AccountRepository {
        private val accountsFlow: MutableStateFlow<List<Account>>

        init {
            val initialAccounts =
                listOf(
                    Account(
                        id = "bca",
                        name = "BCA Personal",
                        balance = 12400000L,
                        type = "BCA Personal",
                        iconType = "BCA",
                        isVisible = true,
                        isPrimary = true,
                    ),
                    Account(
                        id = "mandiri",
                        name = "Mandiri Tabungan",
                        balance = 8150000L,
                        type = "Mandiri Tabungan",
                        iconType = "Mandiri",
                        isVisible = true,
                        isPrimary = false,
                    ),
                    Account(
                        id = "gopay",
                        name = "GoPay",
                        balance = 2200000L,
                        type = "GoPay",
                        iconType = "GoPay",
                        isVisible = true,
                        isPrimary = false,
                    ),
                    Account(
                        id = "tunai",
                        name = "Uang Tunai",
                        balance = 1500000L,
                        type = "Uang Tunai",
                        iconType = "Tunai",
                        isVisible = true,
                        isPrimary = false,
                    ),
                )
            accountsFlow = MutableStateFlow(initialAccounts)
        }

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
    }
