package com.flla.zenspend.feature.transaction

import com.flla.zenspend.core.domain.usecase.ObserveAccountsUseCase
import com.flla.zenspend.core.domain.usecase.ObserveCategoriesUseCase
import com.flla.zenspend.core.domain.usecase.SaveTransactionUseCase
import com.flla.zenspend.core.model.Account
import com.flla.zenspend.core.model.Category
import com.flla.zenspend.core.testing.MainDispatcherRule
import com.flla.zenspend.core.testing.repository.FakeAccountRepository
import com.flla.zenspend.core.testing.repository.FakeCategoryRepository
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
    private lateinit var fakeAccountRepository: FakeAccountRepository
    private lateinit var fakeCategoryRepository: FakeCategoryRepository
    private lateinit var observeAccountsUseCase: ObserveAccountsUseCase
    private lateinit var observeCategoriesUseCase: ObserveCategoriesUseCase
    private lateinit var saveTransactionUseCase: SaveTransactionUseCase
    private lateinit var viewModel: TransactionViewModel

    @Before
    fun setUp() {
        fakeTransactionRepository = FakeTransactionRepository()
        fakeAccountRepository = FakeAccountRepository()
        fakeCategoryRepository = FakeCategoryRepository()
        fakeAccountRepository.setAccounts(
            listOf(
                Account("acc_tunai", "Tunai", 1_000_000L, "Cash", "Tunai", isVisible = true, isPrimary = true),
                Account("acc_bca", "BCA Personal", 2_000_000L, "Bank", "BCA", isVisible = true, isPrimary = false),
            ),
        )
        fakeCategoryRepository.setCategories(
            listOf(
                Category("cat_makan", "Makanan", "restaurant", "#E65100", isIncome = false, isPrimary = true),
            ),
        )
        observeAccountsUseCase = ObserveAccountsUseCase(fakeAccountRepository)
        observeCategoriesUseCase = ObserveCategoriesUseCase(fakeCategoryRepository)
        saveTransactionUseCase = SaveTransactionUseCase(fakeTransactionRepository)
        viewModel = TransactionViewModel(observeAccountsUseCase, observeCategoriesUseCase, saveTransactionUseCase)
    }

    @Test
    fun initialize_setsCategoryAndType() =
        runTest {
            viewModel.initialize(categoryId = "cat_makan", isIncome = false)

            val state = viewModel.uiState.value
            assertEquals("cat_makan", state.categoryId)
            assertEquals("Makanan", state.categoryName)
            assertEquals("restaurant", state.categoryIconName)
            assertEquals("#E65100", state.categoryColorHex)
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
            viewModel.initialize(categoryId = "cat_makan", isIncome = false)
            // Amount is default "0"
            viewModel.saveTransaction()

            val state = viewModel.uiState.value
            assertNotNull(state.amountErrorMessage)
            assertEquals(false, state.isSaveSuccess)
        }

    @Test
    fun saveTransaction_savesSuccessfully_whenAmountIsPositive() =
        runTest {
            viewModel.initialize(categoryId = "cat_makan", isIncome = false)
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
            assertEquals("cat_makan", saved.categoryId)
            assertEquals("Makanan", saved.categoryName)
            assertEquals("acc_tunai", saved.accountId)
            assertEquals("Tunai", saved.accountName)
            assertEquals(false, saved.isIncome)
        }
}
