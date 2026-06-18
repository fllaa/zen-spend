package com.flla.zenspend.core.network.mapper

import com.flla.zenspend.core.model.AuthTokens
import com.flla.zenspend.core.model.User
import com.flla.zenspend.core.network.dto.TokenDto
import com.flla.zenspend.core.network.dto.UserDto

fun TokenDto.asExternalModel() =
    AuthTokens(
        accessToken = accessToken,
        refreshToken = refreshToken,
    )

fun UserDto.asExternalModel() =
    User(
        id = id,
        name = name,
        email = email,
        avatarUrl = avatarUrl,
    )
