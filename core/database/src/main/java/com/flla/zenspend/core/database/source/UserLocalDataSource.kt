package com.flla.zenspend.core.database.source

import com.flla.zenspend.core.database.dao.UserDao
import com.flla.zenspend.core.database.entity.asEntity
import com.flla.zenspend.core.database.entity.asExternalModel
import com.flla.zenspend.core.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserLocalDataSource
    @Inject
    constructor(
        private val userDao: UserDao,
    ) {
        fun observeCurrentUser(): Flow<User?> = userDao.observeCurrentUser().map { it?.asExternalModel() }

        suspend fun upsertCurrentUser(user: User) {
            userDao.upsertUser(user.asEntity())
        }

        suspend fun clear() {
            userDao.deleteAll()
        }
    }
