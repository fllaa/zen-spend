package com.flla.zenspend.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flla.zenspend.core.domain.usecase.ObserveAccountsUseCase
import com.flla.zenspend.core.domain.usecase.ObserveCategoriesUseCase
import com.flla.zenspend.core.domain.usecase.ObserveCurrentUserUseCase
import com.flla.zenspend.core.domain.usecase.ObserveTransactionsUseCase
import com.flla.zenspend.core.model.Account
import com.flla.zenspend.core.model.Category
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
    val accounts: List<Account> = emptyList(),
    val categories: List<Category> = emptyList(),
    val searchQuery: String = "",
    val selectedPeriod: String = "Bulan Ini",
    val selectedAccountId: String? = null,
    val selectedCategoryId: String? = null,
    val selectedAccountLabel: String = "Semua Rekening",
    val selectedCategoryLabel: String = "Kategori",
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
        observeAccountsUseCase: ObserveAccountsUseCase,
        observeCategoriesUseCase: ObserveCategoriesUseCase,
        observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    ) : ViewModel() {
        private val searchQueryState = MutableStateFlow("")
        private val selectedPeriodState = MutableStateFlow("Bulan Ini")
        private val selectedAccountState = MutableStateFlow<String?>(null)
        private val selectedCategoryState = MutableStateFlow<String?>(null)

        val uiState: StateFlow<HistoryUiState> =
            combine(
                observeTransactionsUseCase(),
                observeAccountsUseCase(),
                observeCategoriesUseCase(),
                observeCurrentUserUseCase(),
                searchQueryState,
                selectedPeriodState,
                selectedAccountState,
                selectedCategoryState,
            ) { args ->
                @Suppress("UNCHECKED_CAST")
                val transactions = args[0] as List<Transaction>
                val accounts = args[1] as List<Account>
                val categories = args[2] as List<Category>
                val user = args[3] as User?
                val query = args[4] as String
                val period = args[5] as String
                val accountId = args[6] as String?
                val categoryId = args[7] as String?

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
                                tx.categoryName.contains(query, ignoreCase = true)

                        val matchesAccount = accountId == null || tx.accountId == accountId
                        val matchesCategory = categoryId == null || tx.categoryId == categoryId

                        matchesQuery && matchesAccount && matchesCategory
                    }

                // 3. Group transactions by relative dates
                val grouped = groupTransactionsByDate(filtered)

                HistoryUiState(
                    user = user,
                    transactions = filtered,
                    accounts = accounts,
                    categories = categories,
                    searchQuery = query,
                    selectedPeriod = period,
                    selectedAccountId = accountId,
                    selectedCategoryId = categoryId,
                    selectedAccountLabel = accounts.firstOrNull { it.id == accountId }?.name ?: "Semua Rekening",
                    selectedCategoryLabel = categories.firstOrNull { it.id == categoryId }?.name ?: "Kategori",
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

        fun onAccountChange(accountId: String?) {
            selectedAccountState.value = accountId
        }

        fun onCategoryChange(categoryId: String?) {
            selectedCategoryState.value = categoryId
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
