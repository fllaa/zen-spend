package com.flla.zenspend.core.domain.repository

import com.flla.zenspend.core.common.AppResult

interface AuthRepository {
    suspend fun login(
        email: String,
        password: String,
    ): AppResult<Unit>

    suspend fun register(
        name: String,
        email: String,
        password: String,
    ): AppResult<Unit>

    suspend fun logout(): AppResult<Unit>
}
