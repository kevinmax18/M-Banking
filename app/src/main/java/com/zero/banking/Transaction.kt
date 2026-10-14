package com.zero.banking

import com.google.firebase.Timestamp

data class Transaction(
    val id: String = "",
    val uid: String = "",
    val title: String = "",
    val amount: Long = 0,
    val type: String = "IN", // "IN" or "OUT"
    val timestamp: Timestamp? = null
)