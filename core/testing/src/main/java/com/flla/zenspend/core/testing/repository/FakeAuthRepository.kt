package com.flla.zenspend.core.testing.repository

import com.flla.zenspend.core.common.AppResult
import com.flla.zenspend.core.domain.repository.AuthRepository

class FakeAuthRepository : AuthRepository {
    var loginResult: AppResult<Unit> = AppResult.Success(Unit)
    var registerResult: AppResult<Unit> = AppResult.Success(Unit)
    var logoutResult: AppResult<Unit> = AppResult.Success(Unit)

    override suspend fun login(
        email: String,
        password: String,
    ): AppResult<Unit> = loginResult

    override suspend fun register(
        name: String,
        email: String,
        password: String,
    ): AppResult<Unit> = registerResult

    override suspend fun logout(): AppResult<Unit> = logoutResult
}
