package com.flla.zenspend.core.model

data class Category(
    val id: String,
    val name: String,
    val iconName: String,
    val colorHex: String,
    val isIncome: Boolean,
    val isPrimary: Boolean = false,
)
