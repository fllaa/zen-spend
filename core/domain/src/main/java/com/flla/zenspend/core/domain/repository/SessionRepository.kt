package com.flla.zenspend.core.domain.repository

import com.flla.zenspend.core.model.SessionState
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    val sessionState: Flow<SessionState>

    suspend fun markSessionExpired()
}
