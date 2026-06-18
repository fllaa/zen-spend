package com.flla.example.core.network.source

import com.flla.example.core.network.api.AuthApi
import com.flla.example.core.network.dto.LoginRequestDto
import com.flla.example.core.network.dto.RefreshTokenRequestDto
import com.flla.example.core.network.dto.RegisterRequestDto
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
