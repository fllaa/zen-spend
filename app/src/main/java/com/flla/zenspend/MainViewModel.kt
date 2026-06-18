package com.flla.zenspend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flla.zenspend.core.domain.usecase.ObserveSessionUseCase
import com.flla.zenspend.core.domain.usecase.ObserveUserPreferencesUseCase
import com.flla.zenspend.core.model.SessionState
import com.flla.zenspend.core.model.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class MainUiState(
    val sessionState: SessionState = SessionState.Loading,
    val themeMode: ThemeMode = ThemeMode.System,
)

@HiltViewModel
class MainViewModel
    @Inject
    constructor(
        observeSessionUseCase: ObserveSessionUseCase,
        observeUserPreferencesUseCase: ObserveUserPreferencesUseCase,
    ) : ViewModel() {
        val uiState: StateFlow<MainUiState> =
            combine(
                observeSessionUseCase.sessionState,
                observeUserPreferencesUseCase.preferences,
            ) { session, preferences ->
                MainUiState(
                    sessionState = session,
                    themeMode = preferences.themeMode,
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = MainUiState(),
            )
    }
