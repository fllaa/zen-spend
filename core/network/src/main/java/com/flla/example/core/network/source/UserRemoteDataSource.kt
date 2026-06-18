package com.flla.example.core.network.source

import com.flla.example.core.network.api.UserApi
import com.flla.example.core.network.mapper.asExternalModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRemoteDataSource @Inject constructor(
    private val userApi: UserApi,
) {
    suspend fun getCurrentUser() = userApi.getCurrentUser().asExternalModel()
}
