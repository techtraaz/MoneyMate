package com.example.moneymate

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class Summary : AppCompatActivity() {

    private lateinit var txtMonthYear: TextView
    private lateinit var btnPrev: ImageView
    private lateinit var btnNext: ImageView

    private lateinit var txtIncomeSummary: TextView
    private lateinit var txtExpenseSummary: TextView

    private lateinit var storage: TransactionStorage

    private var calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        // Initialize views
        txtMonthYear = findViewById(R.id.txtMonthYear)
        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)

        txtIncomeSummary = findViewById(R.id.txtIncomeSummary)
        txtExpenseSummary = findViewById(R.id.txtExpenseSummary)


        storage = TransactionStorage(this)

        updateMonthYear()

        btnPrev.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateMonthYear()
        }

        btnNext.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateMonthYear()
        }
    }

    private fun updateMonthYear() {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        txtMonthYear.text = sdf.format(calendar.time)
        updateTransactionList()
    }

    private fun updateTransactionList() {
        val allTransactions = storage.getTransactions()
        val filtered = allTransactions.filter {
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.date)
            val cal = Calendar.getInstance().apply { time = date!! }
            cal.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                    cal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
        }.sortedByDescending {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.date)
        }

        // Update category summary
        updateCategorySummary(filtered)
    }

    private fun updateCategorySummary(transactions: List<Transaction>) {
        val incomeTransactions = transactions.filter { it.type == "Income" }
        val expenseTransactions = transactions.filter { it.type == "Expense" }

        val incomeSummary = incomeTransactions.groupBy { it.category }
            .mapValues { it.value.sumOf { t -> t.amount } }
        val expenseSummary = expenseTransactions.groupBy { it.category }
            .mapValues { it.value.sumOf { t -> t.amount } }

        // Show income summary
        if (incomeSummary.isNotEmpty()) {
            txtIncomeSummary.text = "Income Summary:\n" + incomeSummary.entries.joinToString("\n") { "${it.key}: ${it.value}" }
        } else {
            txtIncomeSummary.text = "No income records"
        }

        // Show expense summary
        if (expenseSummary.isNotEmpty()) {
            txtExpenseSummary.text = "Expense Summary:\n" + expenseSummary.entries.joinToString("\n") { "${it.key}: ${it.value}" }
        } else {
            txtExpenseSummary.text = "No expense records"
        }
    }
}
