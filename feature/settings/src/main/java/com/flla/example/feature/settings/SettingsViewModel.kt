package com.flla.example.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flla.example.core.domain.usecase.LogoutUseCase
import com.flla.example.core.domain.usecase.ObserveUserPreferencesUseCase
import com.flla.example.core.domain.usecase.SetThemeModeUseCase
import com.flla.example.core.model.ThemeMode
import com.flla.example.core.model.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val preferences: UserPreferences = UserPreferences(),
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeUserPreferencesUseCase: ObserveUserPreferencesUseCase,
    private val setThemeModeUseCase: SetThemeModeUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> = observeUserPreferencesUseCase.preferences
        .map(::SettingsUiState)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            setThemeModeUseCase(themeMode)
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }
}
