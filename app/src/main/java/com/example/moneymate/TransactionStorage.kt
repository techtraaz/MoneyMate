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
        return gson.fromJson(json, type)
    }
}
