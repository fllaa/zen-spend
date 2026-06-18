package com.flla.zenspend.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flla.zenspend.core.domain.usecase.SetOnboardingCompletedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val hasCompleted: Boolean = false,
)

@HiltViewModel
class OnboardingViewModel
    @Inject
    constructor(
        private val setOnboardingCompletedUseCase: SetOnboardingCompletedUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(OnboardingUiState())
        val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

        fun completeOnboarding() {
            viewModelScope.launch {
                setOnboardingCompletedUseCase(true)
                _uiState.update { it.copy(hasCompleted = true) }
            }
        }
    }
