package com.flla.zenspend.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flla.zenspend.core.common.AppResult
import com.flla.zenspend.core.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false,
)

@HiltViewModel
class RegisterViewModel
    @Inject
    constructor(
        private val registerUseCase: RegisterUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(RegisterUiState())
        val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

        fun onNameChanged(value: String) {
            _uiState.update { it.copy(name = value, errorMessage = null) }
        }

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
                when (val result = registerUseCase(state.name, state.email, state.password)) {
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
