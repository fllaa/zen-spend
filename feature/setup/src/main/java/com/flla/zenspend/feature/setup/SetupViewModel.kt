package com.flla.zenspend.feature.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flla.zenspend.core.domain.usecase.CompleteSetupRequest
import com.flla.zenspend.core.domain.usecase.CompleteSetupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SetupStep {
    CURRENCY,
    ACCOUNT,
    CATEGORIES,
}

enum class AccountType {
    CASH,
    BANK,
    WALLET,
}

data class SetupUiState(
    val step: SetupStep = SetupStep.CURRENCY,
    val selectedCurrency: String = "IDR",
    val accountType: AccountType = AccountType.BANK,
    val accountBalance: String = "",
    val accountName: String = "",
    val selectedCategories: Set<String> = setOf("cat_makan", "cat_transport", "cat_belanja"),
    val hasCompleted: Boolean = false,
)

@HiltViewModel
class SetupViewModel
    @Inject
    constructor(
        private val completeSetupUseCase: CompleteSetupUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(SetupUiState())
        val uiState: StateFlow<SetupUiState> = _uiState.asStateFlow()

        fun selectCurrency(currency: String) {
            _uiState.update { it.copy(selectedCurrency = currency) }
        }

        fun selectAccountType(type: AccountType) {
            _uiState.update { it.copy(accountType = type) }
        }

        fun updateAccountBalance(balance: String) {
            _uiState.update { it.copy(accountBalance = balance) }
        }

        fun updateAccountName(name: String) {
            _uiState.update { it.copy(accountName = name) }
        }

        fun toggleCategory(category: String) {
            _uiState.update { state ->
                val current = state.selectedCategories
                val updated =
                    if (current.contains(category)) {
                        current - category
                    } else {
                        current + category
                    }
                state.copy(selectedCategories = updated)
            }
        }

        fun nextStep() {
            _uiState.update { state ->
                when (state.step) {
                    SetupStep.CURRENCY -> state.copy(step = SetupStep.ACCOUNT)
                    SetupStep.ACCOUNT -> state.copy(step = SetupStep.CATEGORIES)
                    SetupStep.CATEGORIES -> state
                }
            }
        }

        fun previousStep(): Boolean {
            var handled = false
            _uiState.update { state ->
                when (state.step) {
                    SetupStep.CURRENCY -> state
                    SetupStep.ACCOUNT -> {
                        handled = true
                        state.copy(step = SetupStep.CURRENCY)
                    }
                    SetupStep.CATEGORIES -> {
                        handled = true
                        state.copy(step = SetupStep.ACCOUNT)
                    }
                }
            }
            return handled
        }

        fun completeSetup() {
            viewModelScope.launch {
                val state = _uiState.value
                val balance = state.accountBalance.toLongOrNull() ?: 0L
                val defaultAccountName =
                    state.accountName.ifBlank {
                        when (state.accountType) {
                            AccountType.CASH -> "Tunai"
                            AccountType.BANK -> "Rekening Utama"
                            AccountType.WALLET -> "Dompet Digital"
                        }
                    }
                val (accountType, provider) =
                    when (state.accountType) {
                        AccountType.CASH -> "Cash" to "Tunai"
                        AccountType.BANK -> "Bank" to "Bank"
                        AccountType.WALLET -> "Wallet" to "Wallet"
                    }
                completeSetupUseCase(
                    CompleteSetupRequest(
                        currency = state.selectedCurrency,
                        accountName = defaultAccountName,
                        accountType = accountType,
                        provider = provider,
                        initialBalance = balance,
                        selectedCategoryIds = state.selectedCategories,
                    ),
                )
                _uiState.update { it.copy(hasCompleted = true) }
            }
        }
    }
