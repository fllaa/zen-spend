package com.flla.zenspend.core.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String?,
    val avatarUrl: String?,
)
