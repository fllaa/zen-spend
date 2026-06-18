package com.flla.example.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flla.example.core.domain.usecase.ObserveCurrentUserUseCase
import com.flla.example.core.domain.usecase.RefreshCurrentUserUseCase
import com.flla.example.core.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val user: User? = null,
    val isRefreshing: Boolean = false,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val refreshCurrentUserUseCase: RefreshCurrentUserUseCase,
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> = observeCurrentUserUseCase()
        .map { HomeUiState(user = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState(isRefreshing = true))

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            refreshCurrentUserUseCase()
        }
    }
}
