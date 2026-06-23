package com.flla.zenspend.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flla.zenspend.core.domain.usecase.ObserveAccountsUseCase
import com.flla.zenspend.core.domain.usecase.ObserveCategoriesUseCase
import com.flla.zenspend.core.domain.usecase.ObserveCurrentUserUseCase
import com.flla.zenspend.core.domain.usecase.ObserveTransactionsUseCase
import com.flla.zenspend.core.domain.usecase.RefreshCurrentUserUseCase
import com.flla.zenspend.core.model.Account
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

data class HomeUiState(
    val user: User? = null,
    val totalBalance: Long = 0L,
    val totalIncome: Long = 0L,
    val totalExpense: Long = 0L,
    val topCategories: List<HomeTopCategory> = emptyList(),
    val recentTransactions: List<HomeRecentTransaction> = emptyList(),
    val isRefreshing: Boolean = false,
)

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        observeAccountsUseCase: ObserveAccountsUseCase,
        observeCategoriesUseCase: ObserveCategoriesUseCase,
        observeCurrentUserUseCase: ObserveCurrentUserUseCase,
        observeTransactionsUseCase: ObserveTransactionsUseCase,
        private val refreshCurrentUserUseCase: RefreshCurrentUserUseCase,
    ) : ViewModel() {
        val uiState: StateFlow<HomeUiState> =
            combine(
                observeCurrentUserUseCase(),
                observeAccountsUseCase(),
                observeCategoriesUseCase(),
                observeTransactionsUseCase(),
            ) { user, accounts, categories, transactions ->
                buildHomeUiState(
                    user = user,
                    accounts = accounts,
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
            val topCategories =
                currentMonthTransactions.filter { !it.isIncome }
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
                totalBalance = totalBalance,
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                topCategories = topCategories,
                recentTransactions = recentTransactions,
            )
        }
    }
