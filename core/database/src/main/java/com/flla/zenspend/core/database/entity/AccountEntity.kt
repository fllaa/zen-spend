package com.flla.zenspend.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.flla.zenspend.core.model.Account

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey val id: String,
    val name: String,
    val balance: Long,
    val type: String,
    val iconType: String,
    val isVisible: Boolean,
    val isPrimary: Boolean,
    val updatedAtMillis: Long,
)

fun AccountEntity.asExternalModel() =
    Account(
        id = id,
        name = name,
        balance = balance,
        type = type,
        iconType = iconType,
        isVisible = isVisible,
        isPrimary = isPrimary,
    )

fun Account.asEntity(updatedAtMillis: Long = System.currentTimeMillis()) =
    AccountEntity(
        id = id,
        name = name,
        balance = balance,
        type = type,
        iconType = iconType,
        isVisible = isVisible,
        isPrimary = isPrimary,
        updatedAtMillis = updatedAtMillis,
    )
