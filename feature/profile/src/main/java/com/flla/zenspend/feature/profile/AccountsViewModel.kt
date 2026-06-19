package com.flla.zenspend.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flla.zenspend.core.domain.usecase.ObserveAccountsUseCase
import com.flla.zenspend.core.domain.usecase.SaveAccountUseCase
import com.flla.zenspend.core.domain.usecase.ToggleAccountVisibilityUseCase
import com.flla.zenspend.core.model.Account
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class AccountsUiState(
    val accounts: List<Account> = emptyList(),
)

@HiltViewModel
class AccountsViewModel
    @Inject
    constructor(
        observeAccountsUseCase: ObserveAccountsUseCase,
        private val saveAccountUseCase: SaveAccountUseCase,
        private val toggleAccountVisibilityUseCase: ToggleAccountVisibilityUseCase,
    ) : ViewModel() {
        val uiState: StateFlow<AccountsUiState> =
            observeAccountsUseCase()
                .map { AccountsUiState(accounts = it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = AccountsUiState(),
                )

        fun toggleVisibility(accountId: String) {
            viewModelScope.launch {
                toggleAccountVisibilityUseCase(accountId)
            }
        }

        fun saveAccount(
            name: String,
            type: String,
            provider: String,
            initialBalance: Long,
            isPrimary: Boolean,
        ) {
            viewModelScope.launch {
                val newAccount =
                    Account(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        balance = initialBalance,
                        type = type,
                        iconType = provider,
                        isVisible = true,
                        isPrimary = isPrimary,
                    )
                saveAccountUseCase(newAccount)
            }
        }
    }
