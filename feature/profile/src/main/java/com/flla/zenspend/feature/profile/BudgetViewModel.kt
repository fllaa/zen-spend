package com.flla.zenspend.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flla.zenspend.core.domain.repository.BudgetRepository
import com.flla.zenspend.core.domain.repository.CategoryRepository
import com.flla.zenspend.core.domain.repository.TransactionRepository
import com.flla.zenspend.core.model.Budget
import com.flla.zenspend.core.model.BudgetPeriod
import com.flla.zenspend.core.model.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class BudgetUiState(
    val totalBudgetLimit: Long = 0L,
    val totalSpentAmount: Long = 0L,
    val totalRemainingAmount: Long = 0L,
    val totalSpentPercentage: Float = 0f,
    val categoryBudgets: List<CategoryBudgetInfo> = emptyList(),
    val availableCategories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
)

data class CategoryBudgetInfo(
    val budgetId: String,
    val category: Category,
    val limitAmount: Long,
    val spentAmount: Long,
    val remainingAmount: Long,
    val spentPercentage: Float,
)

@HiltViewModel
class BudgetViewModel
    @Inject
    constructor(
        private val budgetRepository: BudgetRepository,
        private val categoryRepository: CategoryRepository,
        private val transactionRepository: TransactionRepository,
    ) : ViewModel() {
        val uiState: StateFlow<BudgetUiState> =
            combine(
                budgetRepository.observeBudgets(),
                categoryRepository.observeCategories(),
                transactionRepository.observeTransactions(),
            ) { budgets, categories, transactions ->
                // Calculate spent per category
                val categoryBudgets =
                    budgets.mapNotNull { budget ->
                        val category = categories.find { it.id == budget.categoryId } ?: return@mapNotNull null

                        // Sum transactions matching category name
                        val categoryTransactions =
                            transactions.filter { tx ->
                                !tx.isIncome && isCategoryMatch(tx.category, category.name)
                            }
                        val spentAmount = categoryTransactions.sumOf { it.amount }
                        val remainingAmount = budget.limitAmount - spentAmount
                        val spentPercentage =
                            if (budget.limitAmount > 0) {
                                (spentAmount.toFloat() / budget.limitAmount.toFloat()).coerceIn(0f, 1f)
                            } else {
                                0f
                            }

                        CategoryBudgetInfo(
                            budgetId = budget.id,
                            category = category,
                            limitAmount = budget.limitAmount,
                            spentAmount = spentAmount,
                            remainingAmount = remainingAmount,
                            spentPercentage = spentPercentage,
                        )
                    }

                val totalBudgetLimit = budgets.sumOf { it.limitAmount }
                val totalSpentAmount = categoryBudgets.sumOf { it.spentAmount }
                val totalRemainingAmount = totalBudgetLimit - totalSpentAmount
                val totalSpentPercentage =
                    if (totalBudgetLimit > 0) {
                        (totalSpentAmount.toFloat() / totalBudgetLimit.toFloat()).coerceIn(0f, 1f)
                    } else {
                        0f
                    }

                // Available categories for new budgets are expense categories that don't have a budget yet
                val expenseCategories = categories.filter { !it.isIncome }
                val budgetedCategoryIds = budgets.map { it.categoryId }.toSet()
                val availableCategories = expenseCategories.filter { it.id !in budgetedCategoryIds }

                BudgetUiState(
                    totalBudgetLimit = totalBudgetLimit,
                    totalSpentAmount = totalSpentAmount,
                    totalRemainingAmount = totalRemainingAmount,
                    totalSpentPercentage = totalSpentPercentage,
                    categoryBudgets = categoryBudgets,
                    availableCategories = availableCategories,
                    isLoading = false,
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = BudgetUiState(isLoading = true),
            )

        fun saveBudget(
            categoryId: String,
            limitAmount: Long,
            period: BudgetPeriod,
        ) {
            viewModelScope.launch {
                val currentBudgets = uiState.value.categoryBudgets
                val existingBudget = currentBudgets.find { it.category.id == categoryId }
                val budgetId = existingBudget?.budgetId ?: "b_${UUID.randomUUID()}"
                val budget =
                    Budget(
                        id = budgetId,
                        categoryId = categoryId,
                        limitAmount = limitAmount,
                        period = period,
                    )
                budgetRepository.saveBudget(budget)
            }
        }

        fun deleteBudget(budgetId: String) {
            viewModelScope.launch {
                budgetRepository.deleteBudget(budgetId)
            }
        }

        private fun isCategoryMatch(
            transactionCategory: String,
            categoryName: String,
        ): Boolean {
            return transactionCategory.equals(categoryName, ignoreCase = true) ||
                transactionCategory.startsWith(categoryName, ignoreCase = true) ||
                categoryName.startsWith(transactionCategory, ignoreCase = true)
        }
    }
