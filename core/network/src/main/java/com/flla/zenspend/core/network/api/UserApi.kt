package com.flla.zenspend.core.network.api

import com.flla.zenspend.core.network.dto.UpdateProfileRequest
import com.flla.zenspend.core.network.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface UserApi {
    @GET("me")
    suspend fun getCurrentUser(): UserDto

    @PUT("me")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): UserDto
}
