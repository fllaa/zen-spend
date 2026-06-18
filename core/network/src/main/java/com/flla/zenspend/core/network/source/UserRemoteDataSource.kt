package com.flla.zenspend.core.network.source

import com.flla.zenspend.core.network.api.UserApi
import com.flla.zenspend.core.network.mapper.asExternalModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRemoteDataSource
    @Inject
    constructor(
        private val userApi: UserApi,
    ) {
        suspend fun getCurrentUser() = userApi.getCurrentUser().asExternalModel()
    }
