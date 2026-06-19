package com.flla.zenspend.core.testing

import com.flla.zenspend.core.model.AuthTokens
import com.flla.zenspend.core.model.User

object SampleData {
    val user =
        User(
            id = "user-1",
            name = "Test User",
            email = "test@example.com",
            phone = null,
            avatarUrl = null,
        )

    val tokens =
        AuthTokens(
            accessToken = "access-token",
            refreshToken = "refresh-token",
        )
}
