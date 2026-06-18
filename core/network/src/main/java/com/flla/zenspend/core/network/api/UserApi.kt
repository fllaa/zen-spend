package com.flla.zenspend.core.network.api

import com.flla.zenspend.core.network.dto.UserDto
import retrofit2.http.GET

interface UserApi {
    @GET("me")
    suspend fun getCurrentUser(): UserDto
}
