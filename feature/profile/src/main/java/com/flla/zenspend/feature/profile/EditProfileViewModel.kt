package com.flla.zenspend.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flla.zenspend.core.common.onFailure
import com.flla.zenspend.core.common.onSuccess
import com.flla.zenspend.core.domain.usecase.ObserveCurrentUserUseCase
import com.flla.zenspend.core.domain.usecase.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditProfileUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val isLoading: Boolean = false,
    val isSaveSuccess: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class EditProfileViewModel
    @Inject
    constructor(
        private val observeCurrentUserUseCase: ObserveCurrentUserUseCase,
        private val updateProfileUseCase: UpdateProfileUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(EditProfileUiState())
        val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

        init {
            loadUserProfile()
        }

        private fun loadUserProfile() {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }
                val user = observeCurrentUserUseCase().firstOrNull()
                if (user != null) {
                    _uiState.update {
                        it.copy(
                            name = user.name,
                            email = user.email,
                            phone = user.phone ?: "",
                            isLoading = false,
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }

        fun onNameChange(name: String) {
            _uiState.update { it.copy(name = name, errorMessage = null, isSaveSuccess = false) }
        }

        fun onEmailChange(email: String) {
            _uiState.update { it.copy(email = email, errorMessage = null, isSaveSuccess = false) }
        }

        fun onPhoneChange(phone: String) {
            _uiState.update { it.copy(phone = phone, errorMessage = null, isSaveSuccess = false) }
        }

        fun saveProfile() {
            val state = _uiState.value
            if (state.name.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Nama lengkap tidak boleh kosong") }
                return
            }
            if (state.email.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Email tidak boleh kosong") }
                return
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null, isSaveSuccess = false) }
            viewModelScope.launch {
                updateProfileUseCase(
                    name = state.name,
                    email = state.email,
                    phone = state.phone.ifBlank { null },
                ).onSuccess {
                    _uiState.update { it.copy(isLoading = false, isSaveSuccess = true) }
                }.onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Gagal memperbarui profil: ${error::class.simpleName}",
                        )
                    }
                }
            }
        }

        fun resetSaveSuccess() {
            _uiState.update { it.copy(isSaveSuccess = false) }
        }
    }
