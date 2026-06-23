package com.flla.zenspend.feature.profile

import com.flla.zenspend.core.domain.usecase.DeleteBudgetUseCase
import com.flla.zenspend.core.domain.usecase.ObserveBudgetsUseCase
import com.flla.zenspend.core.domain.usecase.ObserveCategoriesUseCase
import com.flla.zenspend.core.domain.usecase.ObserveTransactionsUseCase
import com.flla.zenspend.core.domain.usecase.SaveBudgetUseCase
import com.flla.zenspend.core.model.Budget
import com.flla.zenspend.core.model.BudgetPeriod
import com.flla.zenspend.core.model.Category
import com.flla.zenspend.core.model.Transaction
import com.flla.zenspend.core.testing.MainDispatcherRule
import com.flla.zenspend.core.testing.repository.FakeBudgetRepository
import com.flla.zenspend.core.testing.repository.FakeCategoryRepository
import com.flla.zenspend.core.testing.repository.FakeTransactionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BudgetViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val budgetRepository = FakeBudgetRepository()
    private val categoryRepository = FakeCategoryRepository()
    private val transactionRepository = FakeTransactionRepository()

    private lateinit var viewModel: BudgetViewModel

    private val testCategories =
        listOf(
            Category("cat_makan", "Makanan", "restaurant", "#E65100", isIncome = false, isPrimary = true),
            Category("cat_transport", "Transportasi", "directions_car", "#0D47A1", isIncome = false, isPrimary = true),
            Category("cat_gaji", "Gaji", "account_balance_wallet", "#006064", isIncome = true, isPrimary = true),
        )

    private val testBudgets =
        listOf(
            Budget("b_makan", "cat_makan", 1000000L, BudgetPeriod.MONTHLY),
        )

    private val testTransactions =
        listOf(
            Transaction("1", "Makan Siang", 200000L, "cat_makan", "Makanan", "acc_bca", "BCA", isIncome = false, 1000L),
            Transaction("2", "Gaji", 5000000L, "cat_gaji", "Gaji", "acc_mandiri", "Mandiri", isIncome = true, 2000L),
        )

    @Before
    fun setUp() {
        categoryRepository.setCategories(testCategories)
        budgetRepository.setBudgets(testBudgets)
        transactionRepository.setTransactions(testTransactions)

        viewModel =
            BudgetViewModel(
                observeBudgetsUseCase = ObserveBudgetsUseCase(budgetRepository),
                observeCategoriesUseCase = ObserveCategoriesUseCase(categoryRepository),
                observeTransactionsUseCase = ObserveTransactionsUseCase(transactionRepository),
                saveBudgetUseCase = SaveBudgetUseCase(budgetRepository),
                deleteBudgetUseCase = DeleteBudgetUseCase(budgetRepository),
            )
    }

    @Test
    fun loadBudgets_calculatesSpentAndRemainingCorrectly() =
        runTest {
            backgroundScope.launch { viewModel.uiState.collect {} }
            runCurrent()

            val state = viewModel.uiState.value
            assertEquals(1000000L, state.totalBudgetLimit)
            assertEquals(200000L, state.totalSpentAmount)
            assertEquals(800000L, state.totalRemainingAmount)
            assertEquals(0.2f, state.totalSpentPercentage, 0.001f)

            assertEquals(1, state.categoryBudgets.size)
            val makanBudget = state.categoryBudgets.first()
            assertEquals("cat_makan", makanBudget.category.id)
            assertEquals(200000L, makanBudget.spentAmount)
            assertEquals(800000L, makanBudget.remainingAmount)
            assertEquals(0.2f, makanBudget.spentPercentage, 0.001f)

            // Only "Transport" should be in availableCategories (Gaji is income, Makan has budget)
            assertEquals(1, state.availableCategories.size)
            assertEquals("cat_transport", state.availableCategories.first().id)
        }

    @Test
    fun saveBudget_addsOrUpdatesBudget() =
        runTest {
            backgroundScope.launch { viewModel.uiState.collect {} }
            runCurrent()

            // Save a budget for the available category Transport
            viewModel.saveBudget("cat_transport", 500000L, BudgetPeriod.MONTHLY)
            runCurrent()

            val state = viewModel.uiState.value
            assertEquals(2, state.categoryBudgets.size)
            assertTrue(state.categoryBudgets.any { it.category.id == "cat_transport" && it.limitAmount == 500000L })

            // Save a budget updating the Makan category budget limit
            viewModel.saveBudget("cat_makan", 1500000L, BudgetPeriod.MONTHLY)
            runCurrent()

            val updatedState = viewModel.uiState.value
            val updatedMakan = updatedState.categoryBudgets.find { it.category.id == "cat_makan" }
            assertNotNull(updatedMakan)
            assertEquals(1500000L, updatedMakan!!.limitAmount)
        }

    @Test
    fun deleteBudget_removesBudget() =
        runTest {
            backgroundScope.launch { viewModel.uiState.collect {} }
            runCurrent()

            viewModel.deleteBudget("b_makan")
            runCurrent()

            val state = viewModel.uiState.value
            assertTrue(state.categoryBudgets.isEmpty())
            assertEquals(0L, state.totalBudgetLimit)
        }
}
