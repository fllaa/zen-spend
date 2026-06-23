package com.flla.zenspend.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.flla.zenspend.core.model.Transaction

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val amount: Long,
    val categoryId: String,
    val categoryName: String,
    val accountId: String,
    val accountName: String,
    val isIncome: Boolean,
    val timestamp: Long,
    val note: String?,
    val updatedAtMillis: Long,
)

fun TransactionEntity.asExternalModel() =
    Transaction(
        id = id,
        title = title,
        amount = amount,
        categoryId = categoryId,
        categoryName = categoryName,
        accountId = accountId,
        accountName = accountName,
        isIncome = isIncome,
        timestamp = timestamp,
        note = note,
    )

fun Transaction.asEntity(updatedAtMillis: Long = System.currentTimeMillis()) =
    TransactionEntity(
        id = id,
        title = title,
        amount = amount,
        categoryId = categoryId,
        categoryName = categoryName,
        accountId = accountId,
        accountName = accountName,
        isIncome = isIncome,
        timestamp = timestamp,
        note = note,
        updatedAtMillis = updatedAtMillis,
    )
