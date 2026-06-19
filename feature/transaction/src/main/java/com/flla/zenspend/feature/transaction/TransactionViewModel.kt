package com.flla.zenspend.feature.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flla.zenspend.core.domain.usecase.SaveTransactionUseCase
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
    val category: String = "",
    val isIncome: Boolean = false,
    val selectedAccount: String = "Tunai",
    val dateEpochMillis: Long = System.currentTimeMillis(),
    val note: String = "",
    val isSaveSuccess: Boolean = false,
    val isSaving: Boolean = false,
    val amountErrorMessage: String? = null,
)

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val saveTransactionUseCase: SaveTransactionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    fun initialize(category: String, isIncome: Boolean) {
        _uiState.update {
            it.copy(
                category = category,
                isIncome = isIncome
            )
        }
    }

    fun appendDigit(digit: String) {
        _uiState.update { state ->
            val current = state.amount
            val newAmount = when {
                current == "0" && digit == "000" -> "0"
                current == "0" -> digit
                current.length >= 12 -> current // Limit to 12 digits
                else -> current + digit
            }
            state.copy(
                amount = newAmount,
                amountErrorMessage = null
            )
        }
    }

    fun deleteDigit() {
        _uiState.update { state ->
            val current = state.amount
            val newAmount = if (current.length <= 1) {
                "0"
            } else {
                current.substring(0, current.length - 1)
            }
            state.copy(
                amount = newAmount,
                amountErrorMessage = null
            )
        }
    }

    fun selectAccount(account: String) {
        _uiState.update { it.copy(selectedAccount = account) }
    }

    fun updateDate(epochMillis: Long) {
        _uiState.update { it.copy(dateEpochMillis = epochMillis) }
    }

    fun updateNote(note: String) {
        _uiState.update { it.copy(note = note) }
    }

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
                val noteTitle = state.note.ifBlank {
                    if (state.isIncome) "Pemasukan ${state.category}" else "Pengeluaran ${state.category}"
                }
                val transaction = Transaction(
                    id = UUID.randomUUID().toString(),
                    title = noteTitle,
                    amount = amountVal,
                    category = state.category,
                    account = state.selectedAccount,
                    isIncome = state.isIncome,
                    timestamp = state.dateEpochMillis
                )
                saveTransactionUseCase(transaction)
                _uiState.update { it.copy(isSaveSuccess = true, isSaving = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }
}
