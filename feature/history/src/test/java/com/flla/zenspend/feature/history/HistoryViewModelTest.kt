package com.flla.zenspend.feature.history

import com.flla.zenspend.core.domain.usecase.ObserveAccountsUseCase
import com.flla.zenspend.core.domain.usecase.ObserveCategoriesUseCase
import com.flla.zenspend.core.domain.usecase.ObserveCurrentUserUseCase
import com.flla.zenspend.core.domain.usecase.ObserveTransactionsUseCase
import com.flla.zenspend.core.model.Account
import com.flla.zenspend.core.model.Category
import com.flla.zenspend.core.model.Transaction
import com.flla.zenspend.core.testing.MainDispatcherRule
import com.flla.zenspend.core.testing.repository.FakeAccountRepository
import com.flla.zenspend.core.testing.repository.FakeCategoryRepository
import com.flla.zenspend.core.testing.repository.FakeTransactionRepository
import com.flla.zenspend.core.testing.repository.FakeUserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var fakeTransactionRepository: FakeTransactionRepository
    private lateinit var fakeAccountRepository: FakeAccountRepository
    private lateinit var fakeCategoryRepository: FakeCategoryRepository
    private lateinit var observeTransactionsUseCase: ObserveTransactionsUseCase
    private lateinit var observeAccountsUseCase: ObserveAccountsUseCase
    private lateinit var observeCategoriesUseCase: ObserveCategoriesUseCase
    private lateinit var observeCurrentUserUseCase: ObserveCurrentUserUseCase
    private lateinit var viewModel: HistoryViewModel

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        fakeTransactionRepository = FakeTransactionRepository()
        fakeAccountRepository = FakeAccountRepository()
        fakeCategoryRepository = FakeCategoryRepository()
        observeTransactionsUseCase = ObserveTransactionsUseCase(fakeTransactionRepository)
        observeAccountsUseCase = ObserveAccountsUseCase(fakeAccountRepository)
        observeCategoriesUseCase = ObserveCategoriesUseCase(fakeCategoryRepository)
        observeCurrentUserUseCase = ObserveCurrentUserUseCase(fakeUserRepository)
        fakeAccountRepository.setAccounts(
            listOf(
                Account("acc_bca", "BCA", 1_000_000L, "Bank", "BCA", isVisible = true, isPrimary = true),
                Account("acc_mandiri", "Mandiri", 1_000_000L, "Bank", "Mandiri", isVisible = true, isPrimary = false),
                Account("acc_tunai", "Tunai", 1_000_000L, "Cash", "Tunai", isVisible = true, isPrimary = false),
            ),
        )
        fakeCategoryRepository.setCategories(
            listOf(
                Category("cat_makan", "Makanan", "restaurant", "#E65100", isIncome = false, isPrimary = true),
                Category(
                    "cat_gaji",
                    "Gaji",
                    "account_balance_wallet",
                    "#006064",
                    isIncome = true,
                    isPrimary = true,
                ),
                Category(
                    "cat_transport",
                    "Transportasi",
                    "directions_car",
                    "#0D47A1",
                    isIncome = false,
                    isPrimary = true,
                ),
            ),
        )

        viewModel =
            HistoryViewModel(
                observeTransactionsUseCase = observeTransactionsUseCase,
                observeAccountsUseCase = observeAccountsUseCase,
                observeCategoriesUseCase = observeCategoriesUseCase,
                observeCurrentUserUseCase = observeCurrentUserUseCase,
            )
    }

    @Test
    fun uiState_initiallyExposesEmptyState() =
        runTest {
            val uiState = viewModel.uiState.value
            assertEquals(emptyList<Transaction>(), uiState.transactions)
            assertEquals("", uiState.searchQuery)
            assertEquals("Bulan Ini", uiState.selectedPeriod)
            assertEquals("Semua Rekening", uiState.selectedAccountLabel)
            assertEquals("Kategori", uiState.selectedCategoryLabel)
        }

    @Test
    fun uiState_filtersBySearchQuery() =
        runTest {
            val testTransactions =
                listOf(
                    Transaction("1", "Kopi Starbucks", 50000L, "cat_makan", "Makanan", "acc_bca", "BCA", false, 1000L),
                    Transaction("2", "Gaji", 10000000L, "cat_gaji", "Gaji", "acc_mandiri", "Mandiri", true, 2000L),
                    Transaction(
                        "3",
                        "Bensin",
                        150000L,
                        "cat_transport",
                        "Transportasi",
                        "acc_tunai",
                        "Tunai",
                        false,
                        3000L,
                    ),
                )
            fakeTransactionRepository.setTransactions(testTransactions)

            // Collect once to trigger flow
            viewModel.uiState.first()

            viewModel.onSearchQueryChange("kopi")

            val state = viewModel.uiState.value
            assertEquals(1, state.transactions.size)
            assertEquals("Kopi Starbucks", state.transactions.first().title)
        }

    @Test
    fun uiState_filtersByAccount() =
        runTest {
            val testTransactions =
                listOf(
                    Transaction("1", "Makan Siang", 80000L, "cat_makan", "Makanan", "acc_bca", "BCA", false, 1000L),
                    Transaction("2", "Gaji", 10000000L, "cat_gaji", "Gaji", "acc_mandiri", "Mandiri", true, 2000L),
                )
            fakeTransactionRepository.setTransactions(testTransactions)
            viewModel.uiState.first()

            viewModel.onAccountChange("acc_mandiri")

            val state = viewModel.uiState.value
            assertEquals(1, state.transactions.size)
            assertEquals("Gaji", state.transactions.first().title)
        }

    @Test
    fun uiState_filtersByCategory() =
        runTest {
            val testTransactions =
                listOf(
                    Transaction("1", "Makan Siang", 80000L, "cat_makan", "Makanan", "acc_bca", "BCA", false, 1000L),
                    Transaction("2", "Gaji", 10000000L, "cat_gaji", "Gaji", "acc_mandiri", "Mandiri", true, 2000L),
                )
            fakeTransactionRepository.setTransactions(testTransactions)
            viewModel.uiState.first()

            viewModel.onCategoryChange("cat_makan")

            val state = viewModel.uiState.value
            assertEquals(1, state.transactions.size)
            assertEquals("Makan Siang", state.transactions.first().title)
        }

    @Test
    fun uiState_calculatesSummaryCorrectly() =
        runTest {
            val testTransactions =
                listOf(
                    Transaction("1", "Makan Siang", 100000L, "cat_makan", "Makanan", "acc_bca", "BCA", false, 1000L),
                    Transaction("2", "Gaji", 10000000L, "cat_gaji", "Gaji", "acc_mandiri", "Mandiri", true, 2000L),
                    Transaction(
                        "3",
                        "Bensin",
                        200000L,
                        "cat_transport",
                        "Transportasi",
                        "acc_tunai",
                        "Tunai",
                        false,
                        3000L,
                    ),
                )
            fakeTransactionRepository.setTransactions(testTransactions)
            viewModel.uiState.first()

            val state = viewModel.uiState.value
            assertEquals(300000L, state.totalExpense)
            assertEquals(10000000L, state.totalIncome)
            assertEquals(9700000L, state.remainingBudget)
        }
}
