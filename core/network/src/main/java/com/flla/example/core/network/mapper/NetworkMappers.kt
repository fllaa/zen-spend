package com.flla.example.core.network.mapper

import com.flla.example.core.model.AuthTokens
import com.flla.example.core.model.User
import com.flla.example.core.network.dto.TokenDto
import com.flla.example.core.network.dto.UserDto

fun TokenDto.asExternalModel() = AuthTokens(
    accessToken = accessToken,
    refreshToken = refreshToken,
)

fun UserDto.asExternalModel() = User(
    id = id,
    name = name,
    email = email,
    avatarUrl = avatarUrl,
)
