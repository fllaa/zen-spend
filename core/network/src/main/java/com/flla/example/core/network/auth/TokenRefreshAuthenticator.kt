package com.flla.example.core.network.auth

import com.flla.example.core.datastore.AuthTokenDataSource
import com.flla.example.core.network.api.AuthApi
import com.flla.example.core.network.di.RefreshApi
import com.flla.example.core.network.dto.RefreshTokenRequestDto
import com.flla.example.core.network.mapper.asExternalModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenRefreshAuthenticator @Inject constructor(
    private val tokenDataSource: AuthTokenDataSource,
    @RefreshApi private val refreshApi: AuthApi,
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= MAX_ATTEMPTS) {
            runBlocking { tokenDataSource.markSessionExpired() }
            return null
        }

        val refreshToken = runBlocking { tokenDataSource.tokens.first()?.refreshToken } ?: return null
        val refreshed = runCatching {
            runBlocking { refreshApi.refreshToken(RefreshTokenRequestDto(refreshToken)) }
        }.getOrElse {
            runBlocking { tokenDataSource.markSessionExpired() }
            return null
        }

        val tokens = refreshed.tokens.asExternalModel()
        runBlocking { tokenDataSource.saveTokens(tokens) }

        return response.request
            .newBuilder()
            .header("Authorization", "Bearer ${tokens.accessToken}")
            .build()
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
