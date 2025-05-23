package com.example.moneymate

import android.Manifest
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

class AddFragment : Fragment() {

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

    private val CHANNEL_ID = "budget_alert_channel"
    private val NOTIFICATION_ID = 101

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etAmount = view.findViewById(R.id.etAmount)
        etDate = view.findViewById(R.id.etDate)
        radioIncome = view.findViewById(R.id.radioIncome)
        radioExpense = view.findViewById(R.id.radioExpense)
        spinnerCategory = view.findViewById(R.id.spinnerCategory)
        btnSave = view.findViewById(R.id.btnSaveTransaction)
        btnBackToList = view.findViewById(R.id.btnBackToList)

        val storage = TransactionStorage(requireContext())
        createNotificationChannel()

        arguments?.let {
            editingId = it.getInt("id", -1)
            val editingAmount = it.getDouble("amount", 0.0)
            val editingDate = it.getString("date")
            val editingType = it.getString("type")
            val editingCategory = it.getString("category")

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

                spinnerCategory.post {
                    val index = (spinnerCategory.adapter as ArrayAdapter<String>).getPosition(editingCategory)
                    spinnerCategory.setSelection(index)
                }

                btnSave.text = "Update Transaction"
            } else {
                setCategoryAdapter(listOf("Select category"))
            }
        }

        etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(requireContext(), { _, y, m, d ->
                selectedDate = String.format("%04d-%02d-%02d", y, m + 1, d)
                etDate.setText(selectedDate)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }

        view.findViewById<RadioGroup>(R.id.radioTypeGroup).setOnCheckedChangeListener { _, checkedId ->
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
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
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

            checkIfBudgetExceeded(storage)

            Toast.makeText(requireContext(), "Transaction saved!", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }

        btnBackToList.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setCategoryAdapter(categories: List<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)
        spinnerCategory.adapter = adapter
    }

    private fun getNextId(): Int {
        val prefs = requireContext().getSharedPreferences("idPrefs", AppCompatActivity.MODE_PRIVATE)
        val currentId = prefs.getInt("transaction_id", 0)
        prefs.edit().putInt("transaction_id", currentId + 1).apply()
        return currentId + 1
    }

    private fun getCurrentMonthYear(): String {
        val cal = Calendar.getInstance()
        val sdf = java.text.SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        return sdf.format(cal.time)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun checkIfBudgetExceeded(storage: TransactionStorage) {
        val monthYear = getCurrentMonthYear()
        val totalExpense = storage.getTotalExpenseForMonth(monthYear)
        val budget = storage.getBudgetForMonth(monthYear)

        if (budget > 0 && totalExpense > budget) {
            showBudgetExceededNotification()
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showBudgetExceededNotification() {
        val notification = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Budget Exceeded!")
            .setContentText("Your expenses have exceeded the set budget.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(requireContext()).notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Budget Alerts"
            val descriptionText = "Notifies when expenses exceed budget"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = requireContext().getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
