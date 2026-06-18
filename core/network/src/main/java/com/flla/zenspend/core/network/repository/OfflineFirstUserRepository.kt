package com.flla.zenspend.core.network.repository

import com.flla.zenspend.core.common.AppError
import com.flla.zenspend.core.common.AppResult
import com.flla.zenspend.core.database.source.UserLocalDataSource
import com.flla.zenspend.core.domain.repository.UserRepository
import com.flla.zenspend.core.model.User
import com.flla.zenspend.core.network.source.UserRemoteDataSource
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
