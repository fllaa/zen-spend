package com.flla.zenspend.feature.transaction

import com.flla.zenspend.core.domain.usecase.SaveTransactionUseCase
import com.flla.zenspend.core.testing.MainDispatcherRule
import com.flla.zenspend.core.testing.repository.FakeTransactionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeTransactionRepository: FakeTransactionRepository
    private lateinit var saveTransactionUseCase: SaveTransactionUseCase
    private lateinit var viewModel: TransactionViewModel

    @Before
    fun setUp() {
        fakeTransactionRepository = FakeTransactionRepository()
        saveTransactionUseCase = SaveTransactionUseCase(fakeTransactionRepository)
        viewModel = TransactionViewModel(saveTransactionUseCase)
    }

    @Test
    fun initialize_setsCategoryAndType() =
        runTest {
            viewModel.initialize(category = "Makan", isIncome = false)

            val state = viewModel.uiState.value
            assertEquals("Makan", state.category)
            assertEquals(false, state.isIncome)
        }

    @Test
    fun appendDigit_constructsCorrectAmountString() =
        runTest {
            viewModel.appendDigit("1")
            assertEquals("1", viewModel.uiState.value.amount)

            viewModel.appendDigit("2")
            assertEquals("12", viewModel.uiState.value.amount)

            viewModel.appendDigit("000")
            assertEquals("12000", viewModel.uiState.value.amount)
        }

    @Test
    fun appendDigit_remainsZero_whenFirstDigitIsZeroOrDoubleZero() =
        runTest {
            assertEquals("0", viewModel.uiState.value.amount)

            viewModel.appendDigit("000")
            assertEquals("0", viewModel.uiState.value.amount)

            viewModel.appendDigit("0")
            assertEquals("0", viewModel.uiState.value.amount)
        }

    @Test
    fun appendDigit_limitsAmountLength() =
        runTest {
            repeat(15) {
                viewModel.appendDigit("9")
            }
            assertEquals("999999999999", viewModel.uiState.value.amount) // 12 digits max limit
        }

    @Test
    fun deleteDigit_removesLastDigit() =
        runTest {
            viewModel.appendDigit("5")
            viewModel.appendDigit("2")
            assertEquals("52", viewModel.uiState.value.amount)

            viewModel.deleteDigit()
            assertEquals("5", viewModel.uiState.value.amount)

            viewModel.deleteDigit()
            assertEquals("0", viewModel.uiState.value.amount)

            viewModel.deleteDigit()
            assertEquals("0", viewModel.uiState.value.amount)
        }

    @Test
    fun saveTransaction_fails_whenAmountIsZero() =
        runTest {
            viewModel.initialize(category = "Makan", isIncome = false)
            // Amount is default "0"
            viewModel.saveTransaction()

            val state = viewModel.uiState.value
            assertNotNull(state.amountErrorMessage)
            assertEquals(false, state.isSaveSuccess)
        }

    @Test
    fun saveTransaction_savesSuccessfully_whenAmountIsPositive() =
        runTest {
            viewModel.initialize(category = "Makan", isIncome = false)
            viewModel.appendDigit("5")
            viewModel.appendDigit("000") // Rp 5.000
            viewModel.updateNote("Makan Bakso")

            viewModel.saveTransaction()

            val state = viewModel.uiState.value
            assertNull(state.amountErrorMessage)
            assertEquals(true, state.isSaveSuccess)

            // Observe that the transaction was written to the repository
            val transactions = fakeTransactionRepository.observeTransactions().first()
            assertEquals(1, transactions.size)
            val saved = transactions.first()
            assertEquals("Makan Bakso", saved.title)
            assertEquals(5000L, saved.amount)
            assertEquals("Makan", saved.category)
            assertEquals(false, saved.isIncome)
        }
}
