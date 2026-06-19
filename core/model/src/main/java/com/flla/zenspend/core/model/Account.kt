package com.flla.zenspend.core.model

data class Account(
    val id: String,
    val name: String,
    val balance: Long,
    // e.g. "BCA Personal", "Mandiri Tabungan", "GoPay", "Uang Tunai"
    val type: String,
    // e.g. "BCA", "Mandiri", "GoPay", "Tunai"
    val iconType: String,
    val isVisible: Boolean = true,
    val isPrimary: Boolean = false,
)
