package com.flla.example.core.network.repository

import com.flla.example.core.common.AppError
import com.flla.example.core.common.AppResult
import com.flla.example.core.database.source.UserLocalDataSource
import com.flla.example.core.domain.repository.UserRepository
import com.flla.example.core.model.User
import com.flla.example.core.network.source.UserRemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineFirstUserRepository
    @Inject
    constructor(
        private val localDataSource: UserLocalDataSource,
        private val remoteDataSource: UserRemoteDataSource,
    ) : UserRepository {
        override fun observeCurrentUser(): Flow<User?> = localDataSource.observeCurrentUser()

        override suspend fun refreshCurrentUser(): AppResult<Unit> =
            runCatching { remoteDataSource.getCurrentUser() }
                .fold(
                    onSuccess = { user ->
                        localDataSource.upsertCurrentUser(user)
                        AppResult.Success(Unit)
                    },
                    onFailure = { throwable ->
                        AppResult.Failure(AppError.NetworkUnavailable, throwable)
                    },
                )
    }
