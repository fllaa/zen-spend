package com.flla.example.core.testing.repository

import com.flla.example.core.domain.repository.SessionRepository
import com.flla.example.core.model.SessionState
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSessionRepository : SessionRepository {
    override val sessionState = MutableStateFlow<SessionState>(SessionState.Unauthenticated)

    override suspend fun markSessionExpired() {
        sessionState.value = SessionState.Expired
    }
}
