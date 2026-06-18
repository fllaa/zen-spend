package com.flla.zenspend.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flla.zenspend.core.common.AppResult
import com.flla.zenspend.core.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "demo@example.com",
    val password: String = "password123",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false,
)

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val loginUseCase: LoginUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(LoginUiState())
        val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

        fun onEmailChanged(value: String) {
            _uiState.update { it.copy(email = value, errorMessage = null) }
        }

        fun onPasswordChanged(value: String) {
            _uiState.update { it.copy(password = value, errorMessage = null) }
        }

        fun submit() {
            val state = _uiState.value
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                when (val result = loginUseCase(state.email, state.password)) {
                    is AppResult.Success ->
                        _uiState.update {
                            it.copy(isLoading = false, isAuthenticated = true)
                        }
                    is AppResult.Failure ->
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = result.error.toMessage())
                        }
                }
            }
        }
    }
