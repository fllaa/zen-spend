package com.flla.zenspend.feature.setup

import com.flla.zenspend.core.domain.repository.UserPreferencesRepository
import com.flla.zenspend.core.domain.usecase.CompleteSetupUseCase
import com.flla.zenspend.core.domain.usecase.ObserveCategoriesUseCase
import com.flla.zenspend.core.domain.usecase.SaveAccountUseCase
import com.flla.zenspend.core.domain.usecase.SaveCategoryUseCase
import com.flla.zenspend.core.domain.usecase.SetCurrencyUseCase
import com.flla.zenspend.core.domain.usecase.SetSetupCompletedUseCase
import com.flla.zenspend.core.model.Budget
import com.flla.zenspend.core.model.BudgetPeriod
import com.flla.zenspend.core.model.Category
import com.flla.zenspend.core.model.Transaction
import com.flla.zenspend.core.model.ThemeMode
import com.flla.zenspend.core.model.UserPreferences
import com.flla.zenspend.core.testing.MainDispatcherRule
import com.flla.zenspend.core.testing.repository.FakeAccountRepository
import com.flla.zenspend.core.testing.repository.FakeBudgetRepository
import com.flla.zenspend.core.testing.repository.FakeCategoryRepository
import com.flla.zenspend.core.testing.repository.FakeTransactionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SetupViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userPreferencesRepository = FakeUserPreferencesRepository()
    private val accountRepository = FakeAccountRepository()
    private val budgetRepository = FakeBudgetRepository()
    private val categoryRepository = FakeCategoryRepository()
    private val transactionRepository = FakeTransactionRepository()
    private val completeSetupUseCase =
        CompleteSetupUseCase(
            setCurrencyUseCase = SetCurrencyUseCase(userPreferencesRepository),
            saveAccountUseCase = SaveAccountUseCase(accountRepository),
            observeCategoriesUseCase = ObserveCategoriesUseCase(categoryRepository),
            saveCategoryUseCase = SaveCategoryUseCase(categoryRepository),
            accountRepository = accountRepository,
            budgetRepository = budgetRepository,
            transactionRepository = transactionRepository,
            setSetupCompletedUseCase = SetSetupCompletedUseCase(userPreferencesRepository),
        )
    private val viewModel = SetupViewModel(completeSetupUseCase)

    init {
        categoryRepository.setCategories(
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
                Category("cat_hiburan", "Hiburan", "movie", "#004D40", isIncome = false, isPrimary = false),
            ),
        )
    }

    @Test
    fun initialState_isCorrect() {
        val state = viewModel.uiState.value
        assertEquals(SetupStep.CURRENCY, state.step)
        assertEquals("IDR", state.selectedCurrency)
        assertEquals(AccountType.BANK, state.accountType)
        assertEquals("", state.accountBalance)
        assertEquals("", state.accountName)
        assertEquals(setOf("cat_makan", "cat_transport", "cat_belanja"), state.selectedCategories)
        assertFalse(state.hasCompleted)
    }

    @Test
    fun selectCurrency_updatesState() {
        viewModel.selectCurrency("USD")
        assertEquals("USD", viewModel.uiState.value.selectedCurrency)
    }

    @Test
    fun selectAccountType_updatesState() {
        viewModel.selectAccountType(AccountType.CASH)
        assertEquals(AccountType.CASH, viewModel.uiState.value.accountType)
    }

    @Test
    fun updateAccountBalance_updatesState() {
        viewModel.updateAccountBalance("150000")
        assertEquals("150000", viewModel.uiState.value.accountBalance)
    }

    @Test
    fun updateAccountName_updatesState() {
        viewModel.updateAccountName("BCA")
        assertEquals("BCA", viewModel.uiState.value.accountName)
    }

    @Test
    fun toggleCategory_addsAndRemovesCategory() {
        // "cat_makan" is selected by default, toggling it should remove it
        viewModel.toggleCategory("cat_makan")
        assertFalse(viewModel.uiState.value.selectedCategories.contains("cat_makan"))

        // "cat_hiburan" is not selected, toggling it should add it
        viewModel.toggleCategory("cat_hiburan")
        assertTrue(viewModel.uiState.value.selectedCategories.contains("cat_hiburan"))
    }

    @Test
    fun stepProgression_worksCorrectly() {
        assertEquals(SetupStep.CURRENCY, viewModel.uiState.value.step)

        viewModel.nextStep()
        assertEquals(SetupStep.ACCOUNT, viewModel.uiState.value.step)

        viewModel.nextStep()
        assertEquals(SetupStep.CATEGORIES, viewModel.uiState.value.step)

        assertTrue(viewModel.previousStep())
        assertEquals(SetupStep.ACCOUNT, viewModel.uiState.value.step)

        assertTrue(viewModel.previousStep())
        assertEquals(SetupStep.CURRENCY, viewModel.uiState.value.step)

        assertFalse(viewModel.previousStep())
        assertEquals(SetupStep.CURRENCY, viewModel.uiState.value.step)
    }

    @Test
    fun completeSetup_updatesStateAndRepository() =
        runTest {
            accountRepository.setAccounts(
                listOf(
                    com.flla.zenspend.core.model.Account(
                        id = "demo-account",
                        name = "Demo",
                        balance = 10_000L,
                        type = "Bank",
                        iconType = "Demo",
                        isVisible = true,
                        isPrimary = true,
                    ),
                ),
            )
            budgetRepository.setBudgets(
                listOf(Budget("demo-budget", "cat_makan", 100_000L, BudgetPeriod.MONTHLY)),
            )
            transactionRepository.setTransactions(
                listOf(
                    Transaction(
                        id = "demo-transaction",
                        title = "Demo",
                        amount = 5_000L,
                        categoryId = "cat_makan",
                        categoryName = "Makanan",
                        accountId = "demo-account",
                        accountName = "Demo",
                        isIncome = false,
                        timestamp = 1_000L,
                    ),
                ),
            )
            viewModel.updateAccountName("Dompet Utama")
            viewModel.updateAccountBalance("150000")
            viewModel.completeSetup()
            runCurrent()

            assertTrue(viewModel.uiState.value.hasCompleted)
            assertEquals("IDR", userPreferencesRepository.preferences.first().currency)
            assertTrue(userPreferencesRepository.preferences.first().hasCompletedSetup)
            assertEquals(1, accountRepository.observeAccounts().first().size)
            assertEquals("Dompet Utama", accountRepository.observeAccounts().first().first().name)
            assertTrue(budgetRepository.observeBudgets().first().isEmpty())
            assertTrue(transactionRepository.observeTransactions().first().isEmpty())
            val updatedCategories = categoryRepository.observeCategories().first()
            assertTrue(updatedCategories.first { it.id == "cat_makan" }.isPrimary)
        }
}

class FakeUserPreferencesRepository : UserPreferencesRepository {
    private val _preferences = MutableStateFlow(UserPreferences())
    override val preferences: Flow<UserPreferences> = _preferences

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        _preferences.value = _preferences.value.copy(themeMode = themeMode)
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        _preferences.value = _preferences.value.copy(hasCompletedOnboarding = completed)
    }

    override suspend fun setSetupCompleted(completed: Boolean) {
        _preferences.value = _preferences.value.copy(hasCompletedSetup = completed)
    }

    override suspend fun setCurrency(currency: String) {
        _preferences.value = _preferences.value.copy(currency = currency)
    }
}
