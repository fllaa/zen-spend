package com.flla.zenspend.feature.history

import com.flla.zenspend.core.domain.repository.TransactionRepository
import com.flla.zenspend.core.domain.usecase.ObserveCurrentUserUseCase
import com.flla.zenspend.core.domain.usecase.ObserveTransactionsUseCase
import com.flla.zenspend.core.model.Transaction
import com.flla.zenspend.core.testing.MainDispatcherRule
import com.flla.zenspend.core.testing.repository.FakeUserRepository
import com.flla.zenspend.core.testing.repository.FakeTransactionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
    private lateinit var observeTransactionsUseCase: ObserveTransactionsUseCase
    private lateinit var observeCurrentUserUseCase: ObserveCurrentUserUseCase
    private lateinit var viewModel: HistoryViewModel

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        fakeTransactionRepository = FakeTransactionRepository()
        observeTransactionsUseCase = ObserveTransactionsUseCase(fakeTransactionRepository)
        observeCurrentUserUseCase = ObserveCurrentUserUseCase(fakeUserRepository)

        viewModel =
            HistoryViewModel(
                observeTransactionsUseCase = observeTransactionsUseCase,
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
            assertEquals("Semua Rekening", uiState.selectedAccount)
            assertEquals("Kategori", uiState.selectedCategory)
        }

    @Test
    fun uiState_filtersBySearchQuery() =
        runTest {
            val testTransactions =
                listOf(
                    Transaction("1", "Kopi Starbucks", 50000L, "Makanan", "BCA", false, 1000L),
                    Transaction("2", "Gaji", 10000000L, "Pendapatan", "Mandiri", true, 2000L),
                    Transaction("3", "Bensin", 150000L, "Transportasi", "Tunai", false, 3000L),
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
                    Transaction("1", "Makan Siang", 80000L, "Makanan", "BCA", false, 1000L),
                    Transaction("2", "Gaji", 10000000L, "Pendapatan", "Mandiri", true, 2000L),
                )
            fakeTransactionRepository.setTransactions(testTransactions)
            viewModel.uiState.first()

            viewModel.onAccountChange("Mandiri")

            val state = viewModel.uiState.value
            assertEquals(1, state.transactions.size)
            assertEquals("Gaji", state.transactions.first().title)
        }

    @Test
    fun uiState_filtersByCategory() =
        runTest {
            val testTransactions =
                listOf(
                    Transaction("1", "Makan Siang", 80000L, "Makanan", "BCA", false, 1000L),
                    Transaction("2", "Gaji", 10000000L, "Pendapatan", "Mandiri", true, 2000L),
                )
            fakeTransactionRepository.setTransactions(testTransactions)
            viewModel.uiState.first()

            viewModel.onCategoryChange("Makanan")

            val state = viewModel.uiState.value
            assertEquals(1, state.transactions.size)
            assertEquals("Makan Siang", state.transactions.first().title)
        }

    @Test
    fun uiState_calculatesSummaryCorrectly() =
        runTest {
            val testTransactions =
                listOf(
                    Transaction("1", "Makan Siang", 100000L, "Makanan", "BCA", false, 1000L),
                    Transaction("2", "Gaji", 10000000L, "Pendapatan", "Mandiri", true, 2000L),
                    Transaction("3", "Bensin", 200000L, "Transportasi", "Tunai", false, 3000L),
                )
            fakeTransactionRepository.setTransactions(testTransactions)
            viewModel.uiState.first()

            val state = viewModel.uiState.value
            assertEquals(300000L, state.totalExpense)
            assertEquals(10000000L, state.totalIncome)
            assertEquals(9700000L, state.remainingBudget)
        }
}
