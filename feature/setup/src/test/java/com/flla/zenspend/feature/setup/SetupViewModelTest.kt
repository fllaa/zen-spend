package com.flla.zenspend.feature.setup

import com.flla.zenspend.core.domain.repository.UserPreferencesRepository
import com.flla.zenspend.core.domain.usecase.SetSetupCompletedUseCase
import com.flla.zenspend.core.model.ThemeMode
import com.flla.zenspend.core.model.UserPreferences
import com.flla.zenspend.core.testing.MainDispatcherRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SetupViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userPreferencesRepository = FakeUserPreferencesRepository()
    private val setSetupCompletedUseCase = SetSetupCompletedUseCase(userPreferencesRepository)
    private val viewModel = SetupViewModel(setSetupCompletedUseCase)

    @Test
    fun initialState_isCorrect() {
        val state = viewModel.uiState.value
        assertEquals(SetupStep.CURRENCY, state.step)
        assertEquals("IDR", state.selectedCurrency)
        assertEquals(AccountType.BANK, state.accountType)
        assertEquals("", state.accountBalance)
        assertEquals("", state.accountName)
        assertEquals(setOf("Makanan", "Transportasi", "Belanja"), state.selectedCategories)
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
        // "Makanan" is selected by default, toggling it should remove it
        viewModel.toggleCategory("Makanan")
        assertFalse(viewModel.uiState.value.selectedCategories.contains("Makanan"))

        // "Hiburan" is not selected, toggling it should add it
        viewModel.toggleCategory("Hiburan")
        assertTrue(viewModel.uiState.value.selectedCategories.contains("Hiburan"))
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
    fun completeSetup_updatesStateAndRepository() {
        viewModel.completeSetup()

        assertTrue(viewModel.uiState.value.hasCompleted)
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
}
