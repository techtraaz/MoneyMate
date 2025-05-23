package com.example.moneymate

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class ActivityAdd : AppCompatActivity() {

    private lateinit var etAmount: EditText
    private lateinit var etDate: EditText
    private lateinit var radioIncome: RadioButton
    private lateinit var radioExpense: RadioButton
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnSave: Button
    private lateinit var btnBackToList: Button

    private val incomeCategories = listOf("Select category", "Salary", "Bonus", "Interest", "Gift", "Other")
    private val expenseCategories = listOf("Select category", "Rent", "Groceries", "Transport", "Bills", "Entertainment")

    private var selectedDate = ""
    private var editingId = -1

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

        // Handle edit mode
        editingId = intent.getIntExtra("id", -1)
        val editingAmount = intent.getDoubleExtra("amount", 0.0)
        val editingDate = intent.getStringExtra("date")
        val editingType = intent.getStringExtra("type")
        val editingCategory = intent.getStringExtra("category")

        if (editingId != -1) {
            etAmount.setText(editingAmount.toString())
            etDate.setText(editingDate)
            selectedDate = editingDate ?: ""

            if (editingType == "Income") {
                radioIncome.isChecked = true
                setCategoryAdapter(incomeCategories)
            } else {
                radioExpense.isChecked = true
                setCategoryAdapter(expenseCategories)
            }

            // Set spinner category after adapter has been set
            spinnerCategory.post {
                val index = (spinnerCategory.adapter as ArrayAdapter<String>).getPosition(editingCategory)
                spinnerCategory.setSelection(index)
            }

            btnSave.text = "Update Transaction"
        } else {
            setCategoryAdapter(listOf("Select category")) // Initially empty
        }

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
            } else if (checkedId == R.id.radioExpense) {
                setCategoryAdapter(expenseCategories)
            }
        }

        btnSave.setOnClickListener {
            val amount = etAmount.text.toString().toDoubleOrNull()
            val type = when {
                radioIncome.isChecked -> "Income"
                radioExpense.isChecked -> "Expense"
                else -> ""
            }
            val category = spinnerCategory.selectedItem?.toString() ?: ""

            if (amount == null || selectedDate.isEmpty() || type.isEmpty() || category == "Select category") {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val transactionId = if (editingId != -1) editingId else getNextId()

            val updatedTransaction = Transaction(
                id = transactionId,
                type = type,
                category = category,
                amount = amount,
                date = selectedDate
            )

            val transactions = storage.getTransactions().toMutableList()

            if (editingId != -1) {
                val index = transactions.indexOfFirst { it.id == editingId }
                if (index != -1) {
                    transactions[index] = updatedTransaction
                }
            } else {
                transactions.add(updatedTransaction)
            }

            storage.saveTransactions(transactions)

            Toast.makeText(this, "Transaction saved!", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, Activity3::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
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

    private fun getNextId(): Int {
        val prefs = getSharedPreferences("idPrefs", MODE_PRIVATE)
        val currentId = prefs.getInt("transaction_id", 0)
        prefs.edit().putInt("transaction_id", currentId + 1).apply()
        return currentId + 1
    }
}
