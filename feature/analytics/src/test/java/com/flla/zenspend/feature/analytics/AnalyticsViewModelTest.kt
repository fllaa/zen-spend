package com.flla.zenspend.feature.analytics

import com.flla.zenspend.core.domain.usecase.ObserveBudgetsUseCase
import com.flla.zenspend.core.domain.usecase.ObserveCategoriesUseCase
import com.flla.zenspend.core.domain.usecase.ObserveCurrentUserUseCase
import com.flla.zenspend.core.domain.usecase.ObserveTransactionsUseCase
import com.flla.zenspend.core.model.Budget
import com.flla.zenspend.core.model.BudgetPeriod
import com.flla.zenspend.core.model.Category
import com.flla.zenspend.core.model.Transaction
import com.flla.zenspend.core.testing.MainDispatcherRule
import com.flla.zenspend.core.testing.repository.FakeBudgetRepository
import com.flla.zenspend.core.testing.repository.FakeCategoryRepository
import com.flla.zenspend.core.testing.repository.FakeTransactionRepository
import com.flla.zenspend.core.testing.repository.FakeUserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalCoroutinesApi::class)
class AnalyticsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var fakeTransactionRepository: FakeTransactionRepository
    private lateinit var fakeCategoryRepository: FakeCategoryRepository
    private lateinit var fakeBudgetRepository: FakeBudgetRepository
    private lateinit var observeTransactionsUseCase: ObserveTransactionsUseCase
    private lateinit var observeCategoriesUseCase: ObserveCategoriesUseCase
    private lateinit var observeBudgetsUseCase: ObserveBudgetsUseCase
    private lateinit var observeCurrentUserUseCase: ObserveCurrentUserUseCase
    private lateinit var viewModel: AnalyticsViewModel

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        fakeTransactionRepository = FakeTransactionRepository()
        fakeCategoryRepository = FakeCategoryRepository()
        fakeBudgetRepository = FakeBudgetRepository()
        observeTransactionsUseCase = ObserveTransactionsUseCase(fakeTransactionRepository)
        observeCategoriesUseCase = ObserveCategoriesUseCase(fakeCategoryRepository)
        observeBudgetsUseCase = ObserveBudgetsUseCase(fakeBudgetRepository)
        observeCurrentUserUseCase = ObserveCurrentUserUseCase(fakeUserRepository)
        fakeCategoryRepository.setCategories(
            listOf(
                Category("cat_makan", "Makanan", "restaurant", "#E65100", isIncome = false, isPrimary = true),
                Category(
                    "cat_transport",
                    "Transportasi",
                    "directions_car",
                    "#0D47A1",
                    isIncome = false,
                    isPrimary = true,
                ),
                Category("cat_belanja", "Belanja", "shopping_bag", "#4A148C", isIncome = false, isPrimary = true),
                Category(
                    "cat_gaji",
                    "Gaji",
                    "account_balance_wallet",
                    "#006064",
                    isIncome = true,
                    isPrimary = true,
                ),
            ),
        )
        fakeBudgetRepository.setBudgets(
            listOf(
                Budget("b_makan", "cat_makan", 2_000_000L, BudgetPeriod.MONTHLY),
                Budget("b_transport", "cat_transport", 1_500_000L, BudgetPeriod.MONTHLY),
                Budget("b_belanja", "cat_belanja", 1_500_000L, BudgetPeriod.MONTHLY),
            ),
        )

        viewModel =
            AnalyticsViewModel(
                observeTransactionsUseCase = observeTransactionsUseCase,
                observeCategoriesUseCase = observeCategoriesUseCase,
                observeBudgetsUseCase = observeBudgetsUseCase,
                observeCurrentUserUseCase = observeCurrentUserUseCase,
            )
    }

    @Test
    fun uiState_initiallyExposesEmptyState() =
        runTest {
            val uiState = viewModel.uiState.value
            assertEquals(0L, uiState.totalExpense)
            assertEquals("Bulan", uiState.selectedPeriod)
            assertEquals("-", uiState.largestCategory)
            assertTrue(uiState.categories.isEmpty())
        }

    @Test
    @Suppress("LongMethod")
    fun uiState_calculatesAnalyticsCorrectly() =
        runTest {
            val today = LocalDate.now()
            val zoneId = ZoneId.systemDefault()
            val todayTimestamp = today.atTime(12, 0).atZone(zoneId).toInstant().toEpochMilli()

            val testTransactions =
                listOf(
                    Transaction(
                        "1",
                        "Makan Siang",
                        100000L,
                        "cat_makan",
                        "Makanan",
                        "acc_bca",
                        "BCA",
                        false,
                        todayTimestamp,
                    ),
                    Transaction(
                        "2",
                        "Gaji",
                        10000000L,
                        "cat_gaji",
                        "Gaji",
                        "acc_mandiri",
                        "Mandiri",
                        true,
                        todayTimestamp,
                    ),
                    Transaction(
                        "3",
                        "Bensin",
                        200000L,
                        "cat_transport",
                        "Transportasi",
                        "acc_tunai",
                        "Tunai",
                        false,
                        todayTimestamp,
                    ),
                    Transaction(
                        "4",
                        "Belanja bulanan",
                        300000L,
                        "cat_belanja",
                        "Belanja",
                        "acc_mandiri",
                        "Mandiri",
                        false,
                        todayTimestamp,
                    ),
                )
            fakeTransactionRepository.setTransactions(testTransactions)

            // Trigger combine flow by collecting once
            viewModel.uiState.first()

            val state = viewModel.uiState.value
            assertEquals(600000L, state.totalExpense)
            // Sorted by amount: Belanja (300k) > Transportasi (200k) > Makanan (100k)
            assertEquals("Belanja", state.largestCategory)
            assertEquals(3, state.categories.size)

            // Validate categories details
            val belanjaCat = state.categories.find { it.category == "Belanja" }
            val transportCat = state.categories.find { it.category == "Transportasi" }
            val makananCat = state.categories.find { it.category == "Makanan" }

            assertEquals(300000L, belanjaCat?.amount)
            assertEquals(0.5f, belanjaCat?.percentage) // 300k / 600k = 50%
            assertEquals(0.2f, belanjaCat?.budgetProgress) // 300k / 1.5M = 20%

            assertEquals(200000L, transportCat?.amount)
            assertEquals(100000L, makananCat?.amount)
        }

    @Test
    fun uiState_filtersByPeriod() =
        runTest {
            val today = LocalDate.now()
            val zoneId = ZoneId.systemDefault()
            val todayTimestamp = today.atTime(12, 0).atZone(zoneId).toInstant().toEpochMilli()
            val lastMonthTimestamp = today.minusMonths(1).atTime(12, 0).atZone(zoneId).toInstant().toEpochMilli()

            val testTransactions =
                listOf(
                    Transaction(
                        "1",
                        "Makan Siang",
                        100000L,
                        "cat_makan",
                        "Makanan",
                        "acc_bca",
                        "BCA",
                        false,
                        todayTimestamp,
                    ),
                    Transaction(
                        "2",
                        "Makan Malam Lama",
                        200000L,
                        "cat_makan",
                        "Makanan",
                        "acc_bca",
                        "BCA",
                        false,
                        lastMonthTimestamp,
                    ),
                )
            fakeTransactionRepository.setTransactions(testTransactions)
            viewModel.uiState.first()

            // Default is Bulan (this month)
            assertEquals(100000L, viewModel.uiState.value.totalExpense)

            // Change period to Tahun (should include both)
            viewModel.onPeriodSelected("Tahun")
            val stateTahun = viewModel.uiState.first { it.selectedPeriod == "Tahun" }
            assertEquals(300000L, stateTahun.totalExpense)
        }
}
