package com.flla.zenspend.feature.onboarding

import com.flla.zenspend.core.domain.repository.UserPreferencesRepository
import com.flla.zenspend.core.domain.usecase.SetOnboardingCompletedUseCase
import com.flla.zenspend.core.model.ThemeMode
import com.flla.zenspend.core.model.UserPreferences
import com.flla.zenspend.core.testing.MainDispatcherRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class OnboardingViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userPreferencesRepository = FakeUserPreferencesRepository()
    private val setOnboardingCompletedUseCase = SetOnboardingCompletedUseCase(userPreferencesRepository)
    private val viewModel = OnboardingViewModel(setOnboardingCompletedUseCase)

    @Test
    fun completeOnboarding_updatesStateAndRepository() {
        viewModel.completeOnboarding()

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
}
