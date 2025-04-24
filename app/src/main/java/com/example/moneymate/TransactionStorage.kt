package com.example.moneymate

import android.content.Context
import android.icu.text.SimpleDateFormat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Locale
import kotlin.math.absoluteValue

import android.util.Log


class TransactionStorage(context: Context) {
    private val prefs = context.getSharedPreferences("budget_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val transactionsKey = "transactions"
    private val budgetPrefix = "budget_"

    fun saveTransactions(transactions: List<Transaction>) {
        val json = gson.toJson(transactions)
        prefs.edit().putString(transactionsKey, json).apply()
    }

    fun getTransactions(): List<Transaction> {
        val json = prefs.getString(transactionsKey, null) ?: return emptyList()
        val type = object : TypeToken<List<Transaction>>() {}.type
        val transactions: List<Transaction> = gson.fromJson(json, type)
        val maxId = transactions.maxOfOrNull { it.id } ?: 0
        TransactionManager.resetId(maxId + 1)
        return transactions
    }



    fun deleteTransactionById(id: Int) {
        val transactions = getTransactions().toMutableList()
        val updated = transactions.filter { it.id != id }
        saveTransactions(updated)
    }





    private fun Transaction.matchesMonthYear(monthYear: String): Boolean {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // format of your stored date
            val outputFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault()) // format you want to compare

            val parsedDate = inputFormat.parse(this.date)
            outputFormat.format(parsedDate!!) == monthYear
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }



    fun getTotalExpenseForMay2025(): Double {
        // Define the target month-year string for May 2025
        val targetMonthYear = "April 2025"

        // Filter transactions for the given month and year, and for those that are of type "Expense"
        val expenses = getTransactions()
            .filter { it.type == "Expense" && it.matchesMonthYear(targetMonthYear) }

        // Calculate the total amount of expenses
        val totalExpense = expenses.sumOf { it.amount }

        // Log the total expense


        // Return the total amount of expenses
        return totalExpense
    }


    fun getTotalExpenseForMonth(monthYear: String): Double {
        // Define the target month-year string for May 2025
        val targetMonthYear = monthYear

        // Filter transactions for the given month and year, and for those that are of type "Expense"
        val expenses = getTransactions()
            .filter { it.type == "Expense" && it.matchesMonthYear(targetMonthYear) }

        // Calculate the total amount of expenses
        val totalExpense = expenses.sumOf { it.amount }

        // Log the total expense


        // Return the total amount of expenses
        return totalExpense
    }


    fun setBudgetForMonth(monthYear: String, amount: Double) {
        val key = "$budgetPrefix$monthYear"
        prefs.edit().putFloat(key, amount.toFloat()).apply()
    }


    fun getBudgetForMonth(monthYear: String): Double {
        val key = "$budgetPrefix$monthYear"
        return prefs.getFloat(key, 0f).toDouble() // returns 0.0 if no budget set
    }






}