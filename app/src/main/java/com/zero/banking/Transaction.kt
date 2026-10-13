package com.zero.banking

data class Transaction(
    val title: String,
    val date: String,
    val amount: String,
    val isIncome: Boolean
)