package com.flla.example.core.network.auth

import com.flla.example.core.datastore.AuthTokenDataSource
import com.flla.example.core.network.api.AuthApi
import com.flla.example.core.network.di.RefreshApi
import com.flla.example.core.network.dto.RefreshTokenRequestDto
import com.flla.example.core.network.mapper.asExternalModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenRefreshAuthenticator
    @Inject
    constructor(
        private val tokenDataSource: AuthTokenDataSource,
        @RefreshApi private val refreshApi: AuthApi,
    ) : Authenticator {
        override fun authenticate(
            route: Route?,
            response: Response,
        ): Request? =
            if (responseCount(response) >= MAX_ATTEMPTS) {
                markSessionExpired()
                null
            } else {
                refreshedRequest(response)
            }

        private fun refreshedRequest(response: Response): Request? {
            val refreshed =
                runBlocking { tokenDataSource.tokens.first()?.refreshToken }
                    ?.let(::refreshTokens)

            return refreshed?.let {
                val tokens = it.tokens.asExternalModel()
                runBlocking { tokenDataSource.saveTokens(tokens) }
                response.request
                    .newBuilder()
                    .header("Authorization", "Bearer ${tokens.accessToken}")
                    .build()
            }
        }

        private fun refreshTokens(refreshToken: String) =
            runCatching {
                runBlocking { refreshApi.refreshToken(RefreshTokenRequestDto(refreshToken)) }
            }.getOrElse {
                markSessionExpired()
                null
            }

        private fun markSessionExpired() {
            runBlocking { tokenDataSource.markSessionExpired() }
        }

        private fun responseCount(response: Response): Int {
            var current: Response? = response
            var count = 1
            while (current?.priorResponse != null) {
                count++
                current = current.priorResponse
            }
            return count
        }

        private companion object {
            const val MAX_ATTEMPTS = 2
        }
    }
