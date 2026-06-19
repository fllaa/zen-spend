package com.flla.zenspend.core.domain.repository

import com.flla.zenspend.core.common.AppResult
import com.flla.zenspend.core.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeCurrentUser(): Flow<User?>

    suspend fun refreshCurrentUser(): AppResult<Unit>

    suspend fun updateProfile(
        name: String,
        email: String,
        phone: String?,
    ): AppResult<User>
}
