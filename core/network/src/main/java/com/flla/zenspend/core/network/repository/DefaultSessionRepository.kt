package com.flla.zenspend.core.network.repository

import com.flla.zenspend.core.datastore.AuthTokenDataSource
import com.flla.zenspend.core.domain.repository.SessionRepository
import com.flla.zenspend.core.model.SessionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultSessionRepository
    @Inject
    constructor(
        private val tokenDataSource: AuthTokenDataSource,
    ) : SessionRepository {
        override val sessionState: Flow<SessionState> =
            combine(tokenDataSource.tokens, tokenDataSource.isSessionExpired) { tokens, expired ->
                when {
                    expired -> SessionState.Expired
                    tokens != null -> SessionState.Authenticated
                    else -> SessionState.Unauthenticated
                }
            }.distinctUntilChanged()

        override suspend fun markSessionExpired() {
            tokenDataSource.markSessionExpired()
        }
    }
