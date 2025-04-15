package com.example.moneymate

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Spinner

import android.app.DatePickerDialog
import android.content.Intent

import android.widget.*

import java.util.Calendar


class ActivityAdd : AppCompatActivity() {

    private lateinit var etAmount: EditText
    private lateinit var etDate: EditText
    private lateinit var radioIncome: RadioButton
    private lateinit var radioExpense: RadioButton
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnSave: Button
    private lateinit var btnBackToList: Button

    private val incomeCategories = listOf("Salary", "Bonus", "Interest", "Gift", "Other")
    private val expenseCategories = listOf("Rent", "Groceries", "Transport", "Bills", "Entertainment")

    private var selectedDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        etAmount = findViewById(R.id.etAmount)
        etDate = findViewById(R.id.etDate)
        radioIncome = findViewById(R.id.radioIncome)
        radioExpense = findViewById(R.id.radioExpense)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        btnSave = findViewById(R.id.btnSaveTransaction)
        btnBackToList = findViewById(R.id.btnBackToList)

        val storage = TransactionStorage(this)

        setCategoryAdapter(incomeCategories) // default

        etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(this, { _, y, m, d ->
                selectedDate = String.format("%04d-%02d-%02d", y, m + 1, d)
                etDate.setText(selectedDate)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }

        val radioGroup = findViewById<RadioGroup>(R.id.radioTypeGroup)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radioIncome) {
                setCategoryAdapter(incomeCategories)
            } else {
                setCategoryAdapter(expenseCategories)
            }
        }

        btnSave.setOnClickListener {
            val amount = etAmount.text.toString().toDoubleOrNull()
            if (amount == null || selectedDate.isEmpty()) {
                Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val type = if (radioIncome.isChecked) "Income" else "Expense"
            val category = spinnerCategory.selectedItem.toString()

            val newTransaction = Transaction(type, category, amount, selectedDate)
            val transactions = storage.getTransactions().toMutableList()
            transactions.add(newTransaction)
            storage.saveTransactions(transactions)

            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnBackToList.setOnClickListener {
            startActivity(Intent(this, Activity3::class.java))
        }
    }

    private fun setCategoryAdapter(categories: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spinnerCategory.adapter = adapter
    }
}
