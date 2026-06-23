package com.flla.zenspend.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.flla.zenspend.core.model.Budget
import com.flla.zenspend.core.model.BudgetPeriod

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey val id: String,
    val categoryId: String,
    val limitAmount: Long,
    val period: String,
    val updatedAtMillis: Long,
)

fun BudgetEntity.asExternalModel() =
    Budget(
        id = id,
        categoryId = categoryId,
        limitAmount = limitAmount,
        period = BudgetPeriod.valueOf(period),
    )

fun Budget.asEntity(updatedAtMillis: Long = System.currentTimeMillis()) =
    BudgetEntity(
        id = id,
        categoryId = categoryId,
        limitAmount = limitAmount,
        period = period.name,
        updatedAtMillis = updatedAtMillis,
    )
