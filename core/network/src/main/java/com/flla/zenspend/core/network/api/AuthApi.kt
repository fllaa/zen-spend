package com.flla.zenspend.core.network.api

import com.flla.zenspend.core.network.dto.AuthResponseDto
import com.flla.zenspend.core.network.dto.LoginRequestDto
import com.flla.zenspend.core.network.dto.RefreshTokenRequestDto
import com.flla.zenspend.core.network.dto.RegisterRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequestDto,
    ): AuthResponseDto

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequestDto,
    ): AuthResponseDto

    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequestDto,
    ): AuthResponseDto

    @POST("auth/logout")
    suspend fun logout()
}
