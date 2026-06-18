package com.flla.zenspend.core.model

data class Transaction(
    val id: String,
    val title: String,
    val amount: Long,
    val category: String,
    val account: String,
    val isIncome: Boolean,
    val timestamp: Long,
)
