package com.flla.example.core.network.repository

import com.flla.example.core.common.AppError
import com.flla.example.core.common.AppResult
import com.flla.example.core.database.source.UserLocalDataSource
import com.flla.example.core.datastore.AuthTokenDataSource
import com.flla.example.core.domain.repository.AuthRepository
import com.flla.example.core.network.mapper.asExternalModel
import com.flla.example.core.network.source.AuthRemoteDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultAuthRepository
    @Inject
    constructor(
        private val remoteDataSource: AuthRemoteDataSource,
        private val tokenDataSource: AuthTokenDataSource,
        private val userLocalDataSource: UserLocalDataSource,
    ) : AuthRepository {
        override suspend fun login(
            email: String,
            password: String,
        ): AppResult<Unit> =
            runCatching {
                val response = remoteDataSource.login(email, password)
                tokenDataSource.saveTokens(response.tokens.asExternalModel())
                userLocalDataSource.upsertCurrentUser(response.user.asExternalModel())
            }.fold(
                onSuccess = { AppResult.Success(Unit) },
                onFailure = { AppResult.Failure(AppError.Unknown, it) },
            )

        override suspend fun register(
            name: String,
            email: String,
            password: String,
        ): AppResult<Unit> =
            runCatching {
                val response = remoteDataSource.register(name, email, password)
                tokenDataSource.saveTokens(response.tokens.asExternalModel())
                userLocalDataSource.upsertCurrentUser(response.user.asExternalModel())
            }.fold(
                onSuccess = { AppResult.Success(Unit) },
                onFailure = { AppResult.Failure(AppError.Unknown, it) },
            )

        override suspend fun logout(): AppResult<Unit> =
            runCatching { remoteDataSource.logout() }
                .fold(
                    onSuccess = {
                        tokenDataSource.clearTokens()
                        userLocalDataSource.clear()
                        AppResult.Success(Unit)
                    },
                    onFailure = {
                        tokenDataSource.clearTokens()
                        userLocalDataSource.clear()
                        AppResult.Success(Unit)
                    },
                )
    }
