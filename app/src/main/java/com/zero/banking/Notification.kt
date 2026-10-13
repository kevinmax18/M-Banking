package com.zero.banking

data class Notification(
    val title: String,
    val message: String,
    val type: String // e.g., "PROMO", "SECURITY", "SYSTEM"
)