package com.flla.zenspend.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flla.zenspend.core.domain.usecase.ObserveCurrentUserUseCase
import com.flla.zenspend.core.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
)

@HiltViewModel
class ProfileViewModel
    @Inject
    constructor(
        observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    ) : ViewModel() {
        val uiState: StateFlow<ProfileUiState> =
            observeCurrentUserUseCase()
                .map(::ProfileUiState)
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileUiState())
    }
