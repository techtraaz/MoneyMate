package com.example.moneymate


import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TransactionStorage(context: Context) {
    private val prefs = context.getSharedPreferences("budget_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val key = "transactions"

    fun saveTransactions(transactions: List<Transaction>) {
        val json = gson.toJson(transactions)
        prefs.edit().putString(key, json).apply()
    }

    fun getTransactions(): List<Transaction> {
        val json = prefs.getString(key, null) ?: return emptyList()
        val type = object : TypeToken<List<Transaction>>() {}.type
        val transactions: List<Transaction> = gson.fromJson(json, type)
        val maxId = transactions.maxOfOrNull { it.id } ?: 0
        TransactionManager.resetId(maxId + 1)
        return transactions
    }


    fun saveBudgetForMonth(monthYear: String, budgetAmount: Double) {
        prefs.edit().putFloat(monthYear, budgetAmount.toFloat()).apply()
    }

    // Get the budget for a specific month and year
    fun getBudgetForMonth(monthYear: String): Double {
        return prefs.getFloat(monthYear, 0f).toDouble()
    }

    fun deleteTransactionById(id: Int) {
        val transactions = getTransactions().toMutableList()
        val updated = transactions.filter { it.id != id }
        saveTransactions(updated)
    }

}
