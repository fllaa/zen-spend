package com.flla.zenspend.core.network.source

import com.flla.zenspend.core.network.api.UserApi
import com.flla.zenspend.core.network.dto.UpdateProfileRequest
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

        suspend fun updateProfile(
            name: String,
            email: String,
            phone: String?,
        ) = userApi.updateProfile(UpdateProfileRequest(name, email, phone)).asExternalModel()
    }
