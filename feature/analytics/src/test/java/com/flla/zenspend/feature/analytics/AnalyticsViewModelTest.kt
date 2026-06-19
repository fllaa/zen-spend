package com.flla.zenspend.feature.analytics

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
    private lateinit var observeTransactionsUseCase: ObserveTransactionsUseCase
    private lateinit var observeCurrentUserUseCase: ObserveCurrentUserUseCase
    private lateinit var viewModel: AnalyticsViewModel

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        fakeTransactionRepository = FakeTransactionRepository()
        observeTransactionsUseCase = ObserveTransactionsUseCase(fakeTransactionRepository)
        observeCurrentUserUseCase = ObserveCurrentUserUseCase(fakeUserRepository)

        viewModel =
            AnalyticsViewModel(
                observeTransactionsUseCase = observeTransactionsUseCase,
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
    fun uiState_calculatesAnalyticsCorrectly() =
        runTest {
            val today = LocalDate.now()
            val zoneId = ZoneId.systemDefault()
            val todayTimestamp = today.atTime(12, 0).atZone(zoneId).toInstant().toEpochMilli()

            val testTransactions =
                listOf(
                    Transaction("1", "Makan Siang", 100000L, "Makanan", "BCA", false, todayTimestamp),
                    Transaction("2", "Gaji", 10000000L, "Pendapatan", "Mandiri", true, todayTimestamp),
                    Transaction("3", "Bensin", 200000L, "Transportasi", "Tunai", false, todayTimestamp),
                    Transaction("4", "Belanja bulanan", 300000L, "Kebutuhan", "Mandiri", false, todayTimestamp),
                )
            fakeTransactionRepository.setTransactions(testTransactions)

            // Trigger combine flow by collecting once
            viewModel.uiState.first()

            val state = viewModel.uiState.value
            assertEquals(600000L, state.totalExpense)
            // Sorted by amount: Kebutuhan (300k) > Transportasi (200k) > Makanan (100k)
            assertEquals("Kebutuhan", state.largestCategory)
            assertEquals(3, state.categories.size)

            // Validate categories details
            val kebutuhanCat = state.categories.find { it.category == "Kebutuhan" }
            val transportCat = state.categories.find { it.category == "Transportasi" }
            val makananCat = state.categories.find { it.category == "Makanan" }

            assertEquals(300000L, kebutuhanCat?.amount)
            assertEquals(0.5f, kebutuhanCat?.percentage) // 300k / 600k = 50%
            assertEquals(0.2f, kebutuhanCat?.budgetProgress) // 300k / 1.5M = 20%

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
                    Transaction("1", "Makan Siang", 100000L, "Makanan", "BCA", false, todayTimestamp),
                    Transaction("2", "Makan Malam Lama", 200000L, "Makanan", "BCA", false, lastMonthTimestamp),
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
