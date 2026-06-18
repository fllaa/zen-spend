package com.flla.zenspend.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flla.zenspend.core.domain.usecase.ObserveCurrentUserUseCase
import com.flla.zenspend.core.domain.usecase.ObserveTransactionsUseCase
import com.flla.zenspend.core.model.Transaction
import com.flla.zenspend.core.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class HistoryUiState(
    val user: User? = null,
    val transactions: List<Transaction> = emptyList(),
    val searchQuery: String = "",
    val selectedPeriod: String = "Bulan Ini",
    val selectedAccount: String = "Semua Rekening",
    val selectedCategory: String = "Kategori",
    val totalExpense: Long = 0,
    val totalIncome: Long = 0,
    val remainingBudget: Long = 0,
    val groupedTransactions: Map<String, List<Transaction>> = emptyMap(),
)

@HiltViewModel
class HistoryViewModel
    @Inject
    constructor(
        observeTransactionsUseCase: ObserveTransactionsUseCase,
        observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    ) : ViewModel() {
        private val searchQueryState = MutableStateFlow("")
        private val selectedPeriodState = MutableStateFlow("Bulan Ini")
        private val selectedAccountState = MutableStateFlow("Semua Rekening")
        private val selectedCategoryState = MutableStateFlow("Kategori")

        val uiState: StateFlow<HistoryUiState> =
            combine(
                observeTransactionsUseCase(),
                observeCurrentUserUseCase(),
                searchQueryState,
                selectedPeriodState,
                selectedAccountState,
                selectedCategoryState,
            ) { args ->
                @Suppress("UNCHECKED_CAST")
                val transactions = args[0] as List<Transaction>
                val user = args[1] as User?
                val query = args[2] as String
                val period = args[3] as String
                val account = args[4] as String
                val category = args[5] as String

                // 1. Calculate Monthly Summary
                val totalIncome = transactions.filter { it.isIncome }.sumOf { it.amount }
                val totalExpense = transactions.filter { !it.isIncome }.sumOf { it.amount }
                val remainingBudget = totalIncome - totalExpense

                // 2. Filter transactions based on UI state
                val filtered =
                    transactions.filter { tx ->
                        val matchesQuery =
                            query.isEmpty() ||
                                tx.title.contains(query, ignoreCase = true) ||
                                tx.category.contains(query, ignoreCase = true)

                        val matchesAccount = account == "Semua Rekening" || tx.account == account
                        val matchesCategory = category == "Kategori" || tx.category == category

                        matchesQuery && matchesAccount && matchesCategory
                    }

                // 3. Group transactions by relative dates
                val grouped = groupTransactionsByDate(filtered)

                HistoryUiState(
                    user = user,
                    transactions = filtered,
                    searchQuery = query,
                    selectedPeriod = period,
                    selectedAccount = account,
                    selectedCategory = category,
                    totalExpense = totalExpense,
                    totalIncome = totalIncome,
                    remainingBudget = remainingBudget,
                    groupedTransactions = grouped,
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = HistoryUiState(),
            )

        fun onSearchQueryChange(query: String) {
            searchQueryState.value = query
        }

        fun onPeriodChange(period: String) {
            selectedPeriodState.value = period
        }

        fun onAccountChange(account: String) {
            selectedAccountState.value = account
        }

        fun onCategoryChange(category: String) {
            selectedCategoryState.value = category
        }

        @Suppress("CyclomaticComplexMethod")
        private fun groupTransactionsByDate(list: List<Transaction>): Map<String, List<Transaction>> {
            val today = LocalDate.now()
            val yesterday = today.minusDays(1)
            val zoneId = ZoneId.systemDefault()

            return list.groupBy { tx ->
                val txLocalDate = Instant.ofEpochMilli(tx.timestamp).atZone(zoneId).toLocalDate()
                when (txLocalDate) {
                    today -> "Hari Ini"
                    yesterday -> "Kemarin"
                    else -> {
                        val monthName =
                            when (txLocalDate.monthValue) {
                                1 -> "Jan"
                                2 -> "Feb"
                                3 -> "Mar"
                                4 -> "Apr"
                                5 -> "Mei"
                                6 -> "Jun"
                                7 -> "Jul"
                                8 -> "Agt"
                                9 -> "Sep"
                                10 -> "Okt"
                                11 -> "Nov"
                                12 -> "Des"
                                else -> ""
                            }
                        "${txLocalDate.dayOfMonth} $monthName"
                    }
                }
            }
        }
    }
