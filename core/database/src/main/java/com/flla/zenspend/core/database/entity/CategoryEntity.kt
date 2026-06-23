package com.flla.zenspend.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.flla.zenspend.core.model.Category

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val iconName: String,
    val colorHex: String,
    val isIncome: Boolean,
    val isPrimary: Boolean,
    val updatedAtMillis: Long,
)

fun CategoryEntity.asExternalModel() =
    Category(
        id = id,
        name = name,
        iconName = iconName,
        colorHex = colorHex,
        isIncome = isIncome,
        isPrimary = isPrimary,
    )

fun Category.asEntity(updatedAtMillis: Long = System.currentTimeMillis()) =
    CategoryEntity(
        id = id,
        name = name,
        iconName = iconName,
        colorHex = colorHex,
        isIncome = isIncome,
        isPrimary = isPrimary,
        updatedAtMillis = updatedAtMillis,
    )
