package com.flla.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flla.example.core.domain.usecase.ObserveSessionUseCase
import com.flla.example.core.domain.usecase.ObserveUserPreferencesUseCase
import com.flla.example.core.model.SessionState
import com.flla.example.core.model.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class MainUiState(
    val sessionState: SessionState = SessionState.Loading,
    val themeMode: ThemeMode = ThemeMode.System,
)

@HiltViewModel
class MainViewModel @Inject constructor(
    observeSessionUseCase: ObserveSessionUseCase,
    observeUserPreferencesUseCase: ObserveUserPreferencesUseCase,
) : ViewModel() {
    val uiState: StateFlow<MainUiState> = combine(
        observeSessionUseCase.sessionState,
        observeUserPreferencesUseCase.preferences,
    ) { session, preferences ->
        MainUiState(
            sessionState = session,
            themeMode = preferences.themeMode,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MainUiState())
}
