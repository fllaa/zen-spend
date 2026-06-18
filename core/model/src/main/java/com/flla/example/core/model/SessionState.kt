package com.flla.example.core.model

sealed interface SessionState {
    data object Loading : SessionState
    data object Authenticated : SessionState
    data object Unauthenticated : SessionState
    data object Expired : SessionState
}
