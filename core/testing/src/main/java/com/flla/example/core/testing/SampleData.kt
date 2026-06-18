package com.flla.example.core.testing

import com.flla.example.core.model.AuthTokens
import com.flla.example.core.model.User

object SampleData {
    val user =
        User(
            id = "user-1",
            name = "Test User",
            email = "test@example.com",
            avatarUrl = null,
        )

    val tokens =
        AuthTokens(
            accessToken = "access-token",
            refreshToken = "refresh-token",
        )
}
