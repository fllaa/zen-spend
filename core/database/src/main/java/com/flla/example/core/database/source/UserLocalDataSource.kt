package com.flla.example.core.database.source

import com.flla.example.core.database.dao.UserDao
import com.flla.example.core.database.entity.asEntity
import com.flla.example.core.database.entity.asExternalModel
import com.flla.example.core.model.User
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class UserLocalDataSource @Inject constructor(
    private val userDao: UserDao,
) {
    fun observeCurrentUser(): Flow<User?> =
        userDao.observeCurrentUser().map { it?.asExternalModel() }

    suspend fun upsertCurrentUser(user: User) {
        userDao.upsertUser(user.asEntity())
    }

    suspend fun clear() {
        userDao.deleteAll()
    }
}
