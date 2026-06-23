package com.flla.zenspend.feature.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flla.zenspend.core.domain.usecase.ObserveBudgetsUseCase
import com.flla.zenspend.core.domain.usecase.ObserveCategoriesUseCase
import com.flla.zenspend.core.domain.usecase.ObserveCurrentUserUseCase
import com.flla.zenspend.core.domain.usecase.ObserveTransactionsUseCase
import com.flla.zenspend.core.model.Budget
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
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

data class CategoryBreakdown(
    val categoryId: String,
    val category: String,
    val iconName: String,
    val colorHex: String,
    val amount: Long,
    // Percentage value between 0.0 and 1.0
    val percentage: Float,
    // Budget progress value between 0.0 and 1.0
    val budgetProgress: Float,
    val budgetLimit: Long,
    val transactions: List<Transaction> = emptyList(),
)

data class MonthlyTrend(
    val monthName: String,
    val amount: Long,
    // Height percentage between 0.0 and 1.0 for styling
    val heightPercentage: Float,
    val isCurrent: Boolean,
)

data class ZenInsight(
    val title: String,
    val description: String,
    val type: InsightType,
)

enum class InsightType {
    WARNING,
    SUCCESS,
    INFO,
}

data class AnalyticsUiState(
    val user: User? = null,
    // Selected period: "Minggu", "Bulan", or "Tahun"
    val selectedPeriod: String = "Bulan",
    val totalExpense: Long = 0,
    val dailyAverage: Long = 0,
    val largestCategory: String = "-",
    val categories: List<CategoryBreakdown> = emptyList(),
    val trends: List<MonthlyTrend> = emptyList(),
    val insights: List<ZenInsight> = emptyList(),
)

@HiltViewModel
class AnalyticsViewModel
    @Inject
    constructor(
        observeTransactionsUseCase: ObserveTransactionsUseCase,
        observeCategoriesUseCase: ObserveCategoriesUseCase,
        observeBudgetsUseCase: ObserveBudgetsUseCase,
        observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    ) : ViewModel() {
        private val selectedPeriodState = MutableStateFlow("Bulan")

        val uiState: StateFlow<AnalyticsUiState> =
            combine(
                observeTransactionsUseCase(),
                observeCategoriesUseCase(),
                observeBudgetsUseCase(),
                observeCurrentUserUseCase(),
                selectedPeriodState,
            ) { transactions, categories, budgets, user, period ->
                val zoneId = ZoneId.systemDefault()
                val today = LocalDate.now()

                // 1. Filter transactions based on selected period
                val filteredTransactions =
                    transactions.filter { tx ->
                        val txDate = Instant.ofEpochMilli(tx.timestamp).atZone(zoneId).toLocalDate()
                        when (period) {
                            "Minggu" -> txDate.isAfter(today.minusDays(7)) || txDate.isEqual(today)
                            "Bulan" -> txDate.month == today.month && txDate.year == today.year
                            "Tahun" -> txDate.year == today.year
                            else -> true
                        }
                    }

                val expenses = filteredTransactions.filter { !it.isIncome }
                val totalExpense = expenses.sumOf { it.amount }

                // 2. Daily Average
                val daysInPeriod =
                    when (period) {
                        "Minggu" -> 7L
                        "Bulan" -> today.lengthOfMonth().toLong()
                        "Tahun" -> 365L
                        else -> 30L
                    }
                val dailyAverage = if (totalExpense > 0) totalExpense / daysInPeriod else 0L

                // 3. Category grouping and breakdown
                val categoryMap = categories.associateBy { it.id }
                val budgetMap = budgets.associateBy(Budget::categoryId)
                val categoryGroups = expenses.groupBy { it.categoryId }
                val categoryBreakdowns =
                    categoryGroups.mapNotNull { (categoryId, txs) ->
                        val category = categoryMap[categoryId] ?: return@mapNotNull null
                        val amount = txs.sumOf { it.amount }
                        val pct = if (totalExpense > 0) amount.toFloat() / totalExpense else 0f
                        val budgetLimit = budgetMap[category.id]?.limitAmount ?: 0L
                        val progress =
                            if (budgetLimit > 0) {
                                (amount.toFloat() / budgetLimit).coerceAtMost(1.0f)
                            } else {
                                0f
                            }

                        CategoryBreakdown(
                            categoryId = category.id,
                            category = category.name,
                            iconName = category.iconName,
                            colorHex = category.colorHex,
                            amount = amount,
                            percentage = pct,
                            budgetProgress = progress,
                            budgetLimit = budgetLimit,
                            transactions = txs.sortedByDescending { it.timestamp },
                        )
                    }.sortedByDescending { it.amount }

                val largestCategory = categoryBreakdowns.firstOrNull()?.category ?: "-"

                // 4. Calculate Trends for the last 3 months
                val expenseByMonth =
                    transactions.filter { !it.isIncome }
                        .groupBy { tx ->
                            val txDate = Instant.ofEpochMilli(tx.timestamp).atZone(zoneId).toLocalDate()
                            txDate.year to txDate.monthValue
                        }.mapValues { (_, txs) -> txs.sumOf { it.amount } }
                val trends =
                    (2 downTo 0).map { offset ->
                        val monthDate = today.minusMonths(offset.toLong())
                        val monthName = monthDate.month.getDisplayName(TextStyle.SHORT, Locale("in", "ID"))
                        val isCurrent = offset == 0

                        val amount = expenseByMonth[monthDate.year to monthDate.monthValue] ?: 0L

                        MonthlyTrend(
                            monthName = monthName,
                            amount = amount,
                            // Will calculate relative to max below
                            heightPercentage = 0f,
                            isCurrent = isCurrent,
                        )
                    }

                // Map height percentage relative to max trend amount
                val maxTrend = trends.maxOfOrNull { it.amount } ?: 1L
                val trendsWithHeight =
                    trends.map { t ->
                        t.copy(heightPercentage = (t.amount.toFloat() / maxTrend).coerceIn(0.1f, 1.0f))
                    }

                // 5. Dynamic Zen Insights
                val highestBudgetUsage = categoryBreakdowns.maxByOrNull { it.budgetProgress }
                val insights = buildInsights(totalExpense, largestCategory, highestBudgetUsage)

                AnalyticsUiState(
                    user = user,
                    selectedPeriod = period,
                    totalExpense = totalExpense,
                    dailyAverage = dailyAverage,
                    largestCategory = largestCategory,
                    categories = categoryBreakdowns,
                    trends = trendsWithHeight,
                    insights = insights,
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = AnalyticsUiState(),
            )

        fun onPeriodSelected(period: String) {
            selectedPeriodState.value = period
        }

        private fun buildInsights(
            totalExpense: Long,
            largestCategory: String,
            highestBudgetUsage: CategoryBreakdown?,
        ): List<ZenInsight> {
            val insights = mutableListOf<ZenInsight>()

            if (largestCategory != "-") {
                insights +=
                    ZenInsight(
                        title = "Kategori Terbesar Saat Ini",
                        description = "$largestCategory menjadi kategori pengeluaran terbesar untuk periode ini.",
                        type = InsightType.INFO,
                    )
            }

            highestBudgetUsage?.let { category ->
                if (category.budgetLimit > 0 && category.budgetProgress >= 0.8f) {
                    insights +=
                        ZenInsight(
                            title = "Budget Hampir Habis",
                            description =
                                "Budget ${category.category.lowercase()} " +
                                    "sudah terpakai ${"%.0f".format(category.budgetProgress * 100)}%.",
                            type = InsightType.WARNING,
                        )
                }
            }

            insights +=
                ZenInsight(
                    title = "Ringkasan Pengeluaran",
                    description =
                        "Total pengeluaran tercatat Rp ${formatAmount(totalExpense)} " +
                            "pada periode yang dipilih.",
                    type = InsightType.SUCCESS,
                )

            return insights
        }

        private fun formatAmount(amount: Long): String =
            java.text.NumberFormat.getNumberInstance(Locale("in", "ID")).format(amount)
    }
