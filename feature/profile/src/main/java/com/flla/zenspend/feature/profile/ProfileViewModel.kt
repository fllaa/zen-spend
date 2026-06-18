package com.flla.zenspend.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flla.zenspend.core.domain.usecase.LogoutUseCase
import com.flla.zenspend.core.domain.usecase.ObserveCurrentUserUseCase
import com.flla.zenspend.core.domain.usecase.ObserveUserPreferencesUseCase
import com.flla.zenspend.core.domain.usecase.SetThemeModeUseCase
import com.flla.zenspend.core.model.ThemeMode
import com.flla.zenspend.core.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val themeMode: ThemeMode = ThemeMode.System,
    val notificationsEnabled: Boolean = true,
)

@HiltViewModel
class ProfileViewModel
    @Inject
    constructor(
        observeCurrentUserUseCase: ObserveCurrentUserUseCase,
        observeUserPreferencesUseCase: ObserveUserPreferencesUseCase,
        private val setThemeModeUseCase: SetThemeModeUseCase,
        private val logoutUseCase: LogoutUseCase,
    ) : ViewModel() {
        private val notificationsEnabledState = MutableStateFlow(true)

        val uiState: StateFlow<ProfileUiState> =
            combine(
                observeCurrentUserUseCase(),
                observeUserPreferencesUseCase.preferences,
                notificationsEnabledState,
            ) { user, preferences, notifications ->
                ProfileUiState(
                    user = user,
                    themeMode = preferences.themeMode,
                    notificationsEnabled = notifications,
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ProfileUiState(),
            )

        fun setThemeMode(themeMode: ThemeMode) {
            viewModelScope.launch {
                setThemeModeUseCase(themeMode)
            }
        }

        fun toggleNotifications(enabled: Boolean) {
            notificationsEnabledState.value = enabled
        }

        fun logout() {
            viewModelScope.launch {
                logoutUseCase()
            }
        }
    }
