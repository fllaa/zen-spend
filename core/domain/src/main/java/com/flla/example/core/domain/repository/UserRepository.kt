package com.flla.example.core.domain.repository

import com.flla.example.core.common.AppResult
import com.flla.example.core.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeCurrentUser(): Flow<User?>
    suspend fun refreshCurrentUser(): AppResult<Unit>
}
