package com.example.moneymate

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

class SummaryFragment : Fragment() {

    private lateinit var txtMonthYear: TextView
    private lateinit var btnPrev: ImageView
    private lateinit var btnNext: ImageView

    private lateinit var txtIncomeSummary: TextView
    private lateinit var txtExpenseSummary: TextView

    private lateinit var storage: TransactionStorage

    private var calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtMonthYear = view.findViewById(R.id.txtMonthYear)
        btnPrev = view.findViewById(R.id.btnPrev)
        btnNext = view.findViewById(R.id.btnNext)
        txtIncomeSummary = view.findViewById(R.id.txtIncomeSummary)
        txtExpenseSummary = view.findViewById(R.id.txtExpenseSummary)

        storage = TransactionStorage(requireContext())

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
