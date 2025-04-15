package com.example.moneymate



import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class Activity3 : AppCompatActivity() {

    private lateinit var txtMonthYear: TextView
    private lateinit var btnPrev: ImageView
    private lateinit var btnNext: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private lateinit var storage: TransactionStorage

    private var calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_3)

        txtMonthYear = findViewById(R.id.txtMonthYear)
        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)
        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        storage = TransactionStorage(this)

        loadDummyDataIfNeeded()

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

        adapter = TransactionAdapter(filtered)
        recyclerView.adapter = adapter
    }

    private fun loadDummyDataIfNeeded() {
        if (storage.getTransactions().isEmpty()) {
            val dummy = listOf(
                Transaction("Income", "Salary", 3000.0, "2025-04-05"),
                Transaction("Expense", "Groceries", 150.0, "2025-04-10"),
                Transaction("Income", "Freelance", 1200.0, "2025-04-15"),
                Transaction("Expense", "Rent", 1000.0, "2025-04-01"),
                Transaction("Expense", "Rent", 1000.0, "2025-03-01"),
                Transaction("Expense", "Rent", 1000.0, "2025-03-01")
            )
            storage.saveTransactions(dummy)
        }
    }
}





