package com.flla.example.core.network.api

import com.flla.example.core.network.dto.UserDto
import retrofit2.http.GET

interface UserApi {
    @GET("me")
    suspend fun getCurrentUser(): UserDto
}
