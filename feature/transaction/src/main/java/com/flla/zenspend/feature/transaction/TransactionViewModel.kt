package com.flla.zenspend.feature.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flla.zenspend.core.domain.usecase.ObserveAccountsUseCase
import com.flla.zenspend.core.domain.usecase.ObserveCategoriesUseCase
import com.flla.zenspend.core.domain.usecase.SaveTransactionUseCase
import com.flla.zenspend.core.model.Account
import com.flla.zenspend.core.model.Category
import com.flla.zenspend.core.model.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class TransactionUiState(
    val amount: String = "0",
    val categoryId: String = "",
    val categoryName: String = "",
    val categoryIconName: String = "more_horiz",
    val categoryColorHex: String = "#37474F",
    val isIncome: Boolean = false,
    val availableAccounts: List<Account> = emptyList(),
    val selectedAccountId: String = "",
    val dateEpochMillis: Long = System.currentTimeMillis(),
    val note: String = "",
    val isSaveSuccess: Boolean = false,
    val isSaving: Boolean = false,
    val amountErrorMessage: String? = null,
)

@HiltViewModel
class TransactionViewModel
    @Inject
    constructor(
        observeAccountsUseCase: ObserveAccountsUseCase,
        observeCategoriesUseCase: ObserveCategoriesUseCase,
        private val saveTransactionUseCase: SaveTransactionUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(TransactionUiState())
        val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

        private var latestAccounts: List<Account> = emptyList()
        private var latestCategories: List<Category> = emptyList()

        init {
            viewModelScope.launch {
                observeAccountsUseCase().collect { accounts ->
                    latestAccounts = accounts
                    _uiState.update { state ->
                        val selectedAccountId =
                            state.selectedAccountId
                                .takeIf { id -> accounts.any { it.id == id } }
                                ?: accounts.firstOrNull { it.isPrimary }?.id
                                ?: accounts.firstOrNull()?.id.orEmpty()
                        state.copy(
                            availableAccounts = accounts,
                            selectedAccountId = selectedAccountId,
                        )
                    }
                }
            }

            viewModelScope.launch {
                observeCategoriesUseCase().collect { categories ->
                    latestCategories = categories
                    _uiState.update { state ->
                        val selectedCategory = categories.firstOrNull { it.id == state.categoryId }
                        state.copy(
                            categoryName = selectedCategory?.name ?: state.categoryName,
                            categoryIconName = selectedCategory?.iconName ?: state.categoryIconName,
                            categoryColorHex = selectedCategory?.colorHex ?: state.categoryColorHex,
                        )
                    }
                }
            }
        }

        fun initialize(
            categoryId: String,
            isIncome: Boolean,
        ) {
            _uiState.update {
                val selectedCategory = latestCategories.firstOrNull { category -> category.id == categoryId }
                it.copy(
                    categoryId = categoryId,
                    categoryName = selectedCategory?.name.orEmpty(),
                    categoryIconName = selectedCategory?.iconName ?: it.categoryIconName,
                    categoryColorHex = selectedCategory?.colorHex ?: it.categoryColorHex,
                    isIncome = isIncome,
                )
            }
        }

        fun appendDigit(digit: String) {
            _uiState.update { state ->
                val current = state.amount
                val newAmount =
                    when {
                        current == "0" && digit == "000" -> "0"
                        current == "0" -> digit
                        current.length >= 12 -> current // Limit to 12 digits
                        else -> current + digit
                    }
                state.copy(
                    amount = newAmount,
                    amountErrorMessage = null,
                )
            }
        }

        fun deleteDigit() {
            _uiState.update { state ->
                val current = state.amount
                val newAmount =
                    if (current.length <= 1) {
                        "0"
                    } else {
                        current.substring(0, current.length - 1)
                    }
                state.copy(
                    amount = newAmount,
                    amountErrorMessage = null,
                )
            }
        }

        fun selectAccount(accountId: String) {
            _uiState.update { it.copy(selectedAccountId = accountId) }
        }

        fun updateDate(epochMillis: Long) {
            _uiState.update { it.copy(dateEpochMillis = epochMillis) }
        }

        fun updateNote(note: String) {
            _uiState.update { it.copy(note = note) }
        }

        @Suppress("TooGenericExceptionCaught", "SwallowedException")
        fun saveTransaction() {
            val amountVal = _uiState.value.amount.toLongOrNull() ?: 0L
            if (amountVal <= 0L) {
                _uiState.update { it.copy(amountErrorMessage = "Jumlah harus lebih besar dari 0") }
                return
            }

            _uiState.update { it.copy(isSaving = true) }
            viewModelScope.launch {
                try {
                    val state = _uiState.value
                    val selectedAccount =
                        latestAccounts.firstOrNull { account -> account.id == state.selectedAccountId }
                            ?: latestAccounts.firstOrNull { it.isPrimary }
                            ?: latestAccounts.firstOrNull()
                    val noteTitle =
                        state.note.ifBlank {
                            if (state.isIncome) {
                                "Pemasukan ${state.categoryName.ifBlank { "Tanpa Kategori" }}"
                            } else {
                                "Pengeluaran ${state.categoryName.ifBlank { "Tanpa Kategori" }}"
                            }
                        }
                    val transaction =
                        Transaction(
                            id = UUID.randomUUID().toString(),
                            title = noteTitle,
                            amount = amountVal,
                            categoryId = state.categoryId,
                            categoryName = state.categoryName,
                            accountId = selectedAccount?.id ?: state.selectedAccountId,
                            accountName = selectedAccount?.name.orEmpty(),
                            isIncome = state.isIncome,
                            timestamp = state.dateEpochMillis,
                            note = state.note.ifBlank { null },
                        )
                    saveTransactionUseCase(transaction)
                    _uiState.update { it.copy(isSaveSuccess = true, isSaving = false) }
                } catch (e: Exception) {
                    // Intentional: save errors are non-fatal; state is reset to allow retry
                    _uiState.update { it.copy(isSaving = false) }
                }
            }
        }
    }
