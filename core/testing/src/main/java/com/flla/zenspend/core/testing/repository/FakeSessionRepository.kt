package com.flla.zenspend.core.testing.repository

import com.flla.zenspend.core.domain.repository.SessionRepository
import com.flla.zenspend.core.model.SessionState
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSessionRepository : SessionRepository {
    override val sessionState = MutableStateFlow<SessionState>(SessionState.Unauthenticated)

    override suspend fun markSessionExpired() {
        sessionState.value = SessionState.Expired
    }
}
