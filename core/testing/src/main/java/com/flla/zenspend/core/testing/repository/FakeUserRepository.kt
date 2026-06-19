package com.flla.zenspend.core.testing.repository

import com.flla.zenspend.core.common.AppResult
import com.flla.zenspend.core.domain.repository.UserRepository
import com.flla.zenspend.core.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeUserRepository : UserRepository {
    private val userFlow = MutableStateFlow<User?>(null)
    val currentUser: User? get() = userFlow.value
    var refreshResult: AppResult<Unit> = AppResult.Success(Unit)

    override fun observeCurrentUser(): Flow<User?> = userFlow

    override suspend fun refreshCurrentUser(): AppResult<Unit> = refreshResult

    override suspend fun updateProfile(name: String, email: String, phone: String?): AppResult<User> {
        val updatedUser = User(
            id = userFlow.value?.id ?: "demo-user",
            name = name,
            email = email,
            phone = phone,
            avatarUrl = userFlow.value?.avatarUrl
        )
        userFlow.value = updatedUser
        return AppResult.Success(updatedUser)
    }

    fun setUser(user: User?) {
        userFlow.value = user
    }
}
