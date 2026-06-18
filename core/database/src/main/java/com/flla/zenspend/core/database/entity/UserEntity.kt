package com.flla.zenspend.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.flla.zenspend.core.model.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String?,
    val updatedAtMillis: Long,
)

fun UserEntity.asExternalModel() =
    User(
        id = id,
        name = name,
        email = email,
        avatarUrl = avatarUrl,
    )

fun User.asEntity(updatedAtMillis: Long = System.currentTimeMillis()) =
    UserEntity(
        id = id,
        name = name,
        email = email,
        avatarUrl = avatarUrl,
        updatedAtMillis = updatedAtMillis,
    )
