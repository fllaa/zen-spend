package com.flla.example.core.testing.repository

import com.flla.example.core.common.AppResult
import com.flla.example.core.domain.repository.UserRepository
import com.flla.example.core.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeUserRepository : UserRepository {
    private val userFlow = MutableStateFlow<User?>(null)
    var refreshResult: AppResult<Unit> = AppResult.Success(Unit)

    override fun observeCurrentUser(): Flow<User?> = userFlow

    override suspend fun refreshCurrentUser(): AppResult<Unit> = refreshResult

    fun setUser(user: User?) {
        userFlow.value = user
    }
}
