package com.flla.zenspend.core.network.repository

import com.flla.zenspend.core.common.AppError
import com.flla.zenspend.core.common.AppResult
import com.flla.zenspend.core.database.source.AccountLocalDataSource
import com.flla.zenspend.core.database.source.BudgetLocalDataSource
import com.flla.zenspend.core.database.source.CategoryLocalDataSource
import com.flla.zenspend.core.database.source.TransactionLocalDataSource
import com.flla.zenspend.core.database.source.UserLocalDataSource
import com.flla.zenspend.core.datastore.AuthTokenDataSource
import com.flla.zenspend.core.domain.repository.AuthRepository
import com.flla.zenspend.core.network.mapper.asExternalModel
import com.flla.zenspend.core.network.source.AuthRemoteDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Suppress("LongParameterList")
class DefaultAuthRepository
    @Inject
    constructor(
        private val remoteDataSource: AuthRemoteDataSource,
        private val tokenDataSource: AuthTokenDataSource,
        private val userLocalDataSource: UserLocalDataSource,
        private val accountLocalDataSource: AccountLocalDataSource,
        private val categoryLocalDataSource: CategoryLocalDataSource,
        private val budgetLocalDataSource: BudgetLocalDataSource,
        private val transactionLocalDataSource: TransactionLocalDataSource,
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
                        accountLocalDataSource.clear()
                        categoryLocalDataSource.clear()
                        budgetLocalDataSource.clear()
                        transactionLocalDataSource.clear()
                        AppResult.Success(Unit)
                    },
                    onFailure = {
                        tokenDataSource.clearTokens()
                        userLocalDataSource.clear()
                        accountLocalDataSource.clear()
                        categoryLocalDataSource.clear()
                        budgetLocalDataSource.clear()
                        transactionLocalDataSource.clear()
                        AppResult.Success(Unit)
                    },
                )
    }
