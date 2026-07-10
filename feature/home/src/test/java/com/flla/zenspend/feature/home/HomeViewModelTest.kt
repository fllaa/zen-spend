package com.flla.zenspend.feature.home

import com.flla.zenspend.core.domain.usecase.ObserveAccountsUseCase
import com.flla.zenspend.core.domain.usecase.ObserveBudgetsUseCase
import com.flla.zenspend.core.domain.usecase.ObserveCategoriesUseCase
import com.flla.zenspend.core.domain.usecase.ObserveCurrentUserUseCase
import com.flla.zenspend.core.domain.usecase.ObserveTransactionsUseCase
import com.flla.zenspend.core.domain.usecase.RefreshCurrentUserUseCase
import com.flla.zenspend.core.model.Account
import com.flla.zenspend.core.model.Budget
import com.flla.zenspend.core.model.BudgetPeriod
import com.flla.zenspend.core.model.Category
import com.flla.zenspend.core.model.Transaction
import com.flla.zenspend.core.model.User
import com.flla.zenspend.core.testing.MainDispatcherRule
import com.flla.zenspend.core.testing.repository.FakeAccountRepository
import com.flla.zenspend.core.testing.repository.FakeBudgetRepository
import com.flla.zenspend.core.testing.repository.FakeCategoryRepository
import com.flla.zenspend.core.testing.repository.FakeTransactionRepository
import com.flla.zenspend.core.testing.repository.FakeUserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userRepository = FakeUserRepository()
    private val accountRepository = FakeAccountRepository()
    private val budgetRepository = FakeBudgetRepository()
    private val categoryRepository = FakeCategoryRepository()
    private val transactionRepository = FakeTransactionRepository()

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        val zoneId = ZoneId.systemDefault()
        val today = LocalDate.now()

        userRepository.setUser(
            User(
                id = "user-1",
                name = "Falla",
                email = "falla@example.com",
                phone = null,
                avatarUrl = null,
            ),
        )
        accountRepository.setAccounts(
            listOf(
                Account("acc_bca", "BCA Personal", 2_000_000L, "Bank", "BCA", isVisible = true, isPrimary = true),
                Account("acc_gopay", "GoPay", 500_000L, "Wallet", "GoPay", isVisible = false, isPrimary = false),
            ),
        )
        categoryRepository.setCategories(
            listOf(
                Category("cat_makan", "Makanan", "restaurant", "#E65100", isIncome = false, isPrimary = true),
                Category("cat_transport", "Transportasi", "directions_car", "#0D47A1", isIncome = false, isPrimary = true),
                Category("cat_gaji", "Gaji", "account_balance_wallet", "#006064", isIncome = true, isPrimary = true),
            ),
        )
        budgetRepository.setBudgets(
            listOf(
                Budget("b_makan", "cat_makan", 500_000L, BudgetPeriod.MONTHLY),
                Budget("b_transport", "cat_transport", 300_000L, BudgetPeriod.MONTHLY),
            ),
        )
        transactionRepository.setTransactions(
            listOf(
                Transaction(
                    id = "txn_1",
                    title = "Makan Siang",
                    amount = 100_000L,
                    categoryId = "cat_makan",
                    categoryName = "Makanan",
                    accountId = "acc_bca",
                    accountName = "BCA Personal",
                    isIncome = false,
                    timestamp = today.atTime(12, 0).atZone(zoneId).toInstant().toEpochMilli(),
                ),
                Transaction(
                    id = "txn_2",
                    title = "Naik Ojek",
                    amount = 50_000L,
                    categoryId = "cat_transport",
                    categoryName = "Transportasi",
                    accountId = "acc_bca",
                    accountName = "BCA Personal",
                    isIncome = false,
                    timestamp = today.minusDays(1).atTime(9, 0).atZone(zoneId).toInstant().toEpochMilli(),
                ),
                Transaction(
                    id = "txn_3",
                    title = "Gaji",
                    amount = 4_000_000L,
                    categoryId = "cat_gaji",
                    categoryName = "Gaji",
                    accountId = "acc_bca",
                    accountName = "BCA Personal",
                    isIncome = true,
                    timestamp = today.atTime(8, 0).atZone(zoneId).toInstant().toEpochMilli(),
                ),
            ),
        )

        viewModel =
            HomeViewModel(
                observeAccountsUseCase = ObserveAccountsUseCase(accountRepository),
                observeBudgetsUseCase = ObserveBudgetsUseCase(budgetRepository),
                observeCategoriesUseCase = ObserveCategoriesUseCase(categoryRepository),
                observeCurrentUserUseCase = ObserveCurrentUserUseCase(userRepository),
                observeTransactionsUseCase = ObserveTransactionsUseCase(transactionRepository),
                refreshCurrentUserUseCase = RefreshCurrentUserUseCase(userRepository),
            )
    }

    @Test
    fun uiState_buildsBudgetSummaryAndTopCategoriesFromLocalData() =
        runTest {
            backgroundScope.launch { viewModel.uiState.collect {} }
            runCurrent()

            val state = viewModel.uiState.value
            assertEquals("Falla", state.user?.name)
            assertEquals(2_000_000L, state.totalBalance)
            assertEquals(4_000_000L, state.totalIncome)
            assertEquals(150_000L, state.totalExpense)
            assertEquals(800_000L, state.budgetLimit)
            assertEquals(150_000L, state.budgetSpent)
            assertEquals(650_000L, state.budgetRemaining)
            assertEquals(2, state.topCategories.size)
            assertEquals("Makanan", state.topCategories.first().name)
        }

    @Test
    fun uiState_buildsSevenDayExpenseBars() =
        runTest {
            backgroundScope.launch { viewModel.uiState.collect {} }
            runCurrent()

            val bars = viewModel.uiState.value.weeklyExpenseBars
            assertEquals(7, bars.size)
            assertTrue(bars.any { it.amount == 100_000L })
            assertTrue(bars.any { it.amount == 50_000L })
            assertTrue(bars.last().isHighlighted)
        }
}
