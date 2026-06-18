package com.flla.zenspend.core.network.source

import com.flla.zenspend.core.network.api.AuthApi
import com.flla.zenspend.core.network.dto.LoginRequestDto
import com.flla.zenspend.core.network.dto.RefreshTokenRequestDto
import com.flla.zenspend.core.network.dto.RegisterRequestDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRemoteDataSource
    @Inject
    constructor(
        private val authApi: AuthApi,
    ) {
        suspend fun login(
            email: String,
            password: String,
        ) = authApi.login(LoginRequestDto(email = email, password = password))

        suspend fun register(
            name: String,
            email: String,
            password: String,
        ) = authApi.register(RegisterRequestDto(name = name, email = email, password = password))

        suspend fun refreshToken(refreshToken: String) = authApi.refreshToken(RefreshTokenRequestDto(refreshToken))

        suspend fun logout() {
            authApi.logout()
        }
    }
