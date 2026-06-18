package com.flla.example.core.domain.repository

import com.flla.example.core.model.SessionState
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    val sessionState: Flow<SessionState>

    suspend fun markSessionExpired()
}
