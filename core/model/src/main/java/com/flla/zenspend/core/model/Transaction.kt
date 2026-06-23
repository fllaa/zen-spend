package com.flla.zenspend.core.model

data class Transaction(
    val id: String,
    val title: String,
    val amount: Long,
    val categoryId: String,
    val categoryName: String,
    val accountId: String,
    val accountName: String,
    val isIncome: Boolean,
    val timestamp: Long,
    val note: String? = null,
)
