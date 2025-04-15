package com.example.moneymate


data class Transaction(
    val type: String, // "Income" or "Expense"
    val category: String,
    val amount: Double,
    val date: String // Format: "yyyy-MM-dd"
)
