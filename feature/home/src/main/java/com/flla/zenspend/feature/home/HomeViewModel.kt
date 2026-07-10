package com.flla.zenspend.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flla.zenspend.core.domain.usecase.ObserveAccountsUseCase
import com.flla.zenspend.core.domain.usecase.ObserveBudgetsUseCase
import com.flla.zenspend.core.domain.usecase.ObserveCategoriesUseCase
import com.flla.zenspend.core.domain.usecase.ObserveCurrentUserUseCase
import com.flla.zenspend.core.domain.usecase.ObserveTransactionsUseCase
import com.flla.zenspend.core.domain.usecase.RefreshCurrentUserUseCase
import com.flla.zenspend.core.model.Account
import com.flla.zenspend.core.model.Budget
import com.flla.zenspend.core.model.Category
import com.flla.zenspend.core.model.Transaction
import com.flla.zenspend.core.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

data class HomeTopCategory(
    val name: String,
    val amount: Long,
    val iconName: String,
    val colorHex: String,
)

data class HomeRecentTransaction(
    val title: String,
    val timestamp: Long,
    val amount: Long,
    val isIncome: Boolean,
    val iconName: String,
)

data class HomeStatisticBar(
    val label: String,
    val amount: Long,
    val heightPercentage: Float,
    val isHighlighted: Boolean,
)

data class HomeUiState(
    val user: User? = null,
    val currentMonthLabel: String = "",
    val totalBalance: Long = 0L,
    val totalIncome: Long = 0L,
    val totalExpense: Long = 0L,
    val budgetLimit: Long = 0L,
    val budgetSpent: Long = 0L,
    val budgetRemaining: Long = 0L,
    val budgetProgress: Float = 0f,
    val weeklyExpenseBars: List<HomeStatisticBar> = emptyList(),
    val topCategories: List<HomeTopCategory> = emptyList(),
    val recentTransactions: List<HomeRecentTransaction> = emptyList(),
    val isRefreshing: Boolean = false,
)

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        observeAccountsUseCase: ObserveAccountsUseCase,
        observeBudgetsUseCase: ObserveBudgetsUseCase,
        observeCategoriesUseCase: ObserveCategoriesUseCase,
        observeCurrentUserUseCase: ObserveCurrentUserUseCase,
        observeTransactionsUseCase: ObserveTransactionsUseCase,
        private val refreshCurrentUserUseCase: RefreshCurrentUserUseCase,
    ) : ViewModel() {
        val uiState: StateFlow<HomeUiState> =
            combine(
                observeCurrentUserUseCase(),
                observeAccountsUseCase(),
                observeBudgetsUseCase(),
                observeCategoriesUseCase(),
                observeTransactionsUseCase(),
            ) { user, accounts, budgets, categories, transactions ->
                buildHomeUiState(
                    user = user,
                    accounts = accounts,
                    budgets = budgets,
                    categories = categories,
                    transactions = transactions,
                )
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState(isRefreshing = true))

        init {
            refresh()
        }

        fun refresh() {
            viewModelScope.launch {
                refreshCurrentUserUseCase()
            }
        }

        private fun buildHomeUiState(
            user: User?,
            accounts: List<Account>,
            budgets: List<Budget>,
            categories: List<Category>,
            transactions: List<Transaction>,
        ): HomeUiState {
            val today = LocalDate.now()
            val zoneId = ZoneId.systemDefault()
            val currentMonthTransactions =
                transactions.filter { transaction ->
                    val date = Instant.ofEpochMilli(transaction.timestamp).atZone(zoneId).toLocalDate()
                    date.month == today.month && date.year == today.year
                }
            val totalIncome = currentMonthTransactions.filter { it.isIncome }.sumOf { it.amount }
            val totalExpense = currentMonthTransactions.filter { !it.isIncome }.sumOf { it.amount }
            val totalBalance = accounts.filter { it.isVisible }.sumOf { it.balance }
            val categoryMap = categories.associateBy { it.id }
            val monthlyExpenses = currentMonthTransactions.filter { !it.isIncome }
            val budgetedCategoryIds = budgets.map(Budget::categoryId).toSet()
            val budgetLimit = budgets.sumOf { it.limitAmount }
            val budgetSpent = monthlyExpenses.filter { it.categoryId in budgetedCategoryIds }.sumOf { it.amount }
            val budgetRemaining = budgetLimit - budgetSpent
            val budgetProgress =
                if (budgetLimit > 0L) {
                    (budgetSpent.toFloat() / budgetLimit.toFloat()).coerceIn(0f, 1f)
                } else {
                    0f
                }
            val topCategories =
                monthlyExpenses
                    .groupBy { it.categoryId }
                    .mapNotNull { (categoryId, groupedTransactions) ->
                        val category = categoryMap[categoryId] ?: return@mapNotNull null
                        HomeTopCategory(
                            name = category.name,
                            amount = groupedTransactions.sumOf { it.amount },
                            iconName = category.iconName,
                            colorHex = category.colorHex,
                        )
                    }.sortedByDescending { it.amount }
                    .take(4)
            val weeklyExpenseByDate =
                transactions.filter { !it.isIncome }
                    .groupBy { transaction ->
                        Instant.ofEpochMilli(transaction.timestamp).atZone(zoneId).toLocalDate()
                    }.mapValues { (_, dayTransactions) -> dayTransactions.sumOf { it.amount } }
            val maxWeeklyExpense = (weeklyExpenseByDate.values.maxOrNull() ?: 0L).coerceAtLeast(1L)
            val weeklyExpenseBars =
                (6 downTo 0).map { offset ->
                    val date = today.minusDays(offset.toLong())
                    val amount = weeklyExpenseByDate[date] ?: 0L
                    HomeStatisticBar(
                        label = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("in", "ID")),
                        amount = amount,
                        heightPercentage =
                            if (amount == 0L) {
                                0.12f
                            } else {
                                (amount.toFloat() / maxWeeklyExpense.toFloat()).coerceIn(0.2f, 1f)
                            },
                        isHighlighted = date == today,
                    )
                }
            val recentTransactions =
                transactions.take(5).map { transaction ->
                    val category = categoryMap[transaction.categoryId]
                    HomeRecentTransaction(
                        title = transaction.title,
                        timestamp = transaction.timestamp,
                        amount = transaction.amount,
                        isIncome = transaction.isIncome,
                        iconName =
                            category?.iconName
                                ?: if (transaction.isIncome) "account_balance_wallet" else "more_horiz",
                    )
                }

            return HomeUiState(
                user = user,
                currentMonthLabel = today.month.getDisplayName(TextStyle.FULL, Locale("in", "ID")),
                totalBalance = totalBalance,
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                budgetLimit = budgetLimit,
                budgetSpent = budgetSpent,
                budgetRemaining = budgetRemaining,
                budgetProgress = budgetProgress,
                weeklyExpenseBars = weeklyExpenseBars,
                topCategories = topCategories,
                recentTransactions = recentTransactions,
            )
        }
    }
