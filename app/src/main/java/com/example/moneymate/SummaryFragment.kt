package com.example.moneymate

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.*

class SummaryFragment : Fragment() {

    private lateinit var txtMonthYear: TextView
    private lateinit var btnPrev: ImageView
    private lateinit var btnNext: ImageView
    private lateinit var txtIncomeSummary: TextView
    private lateinit var txtExpenseSummary: TextView
    private lateinit var incomeChart: PieChart
    private lateinit var expenseChart: PieChart

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
        incomeChart = view.findViewById(R.id.incomeChart)
        expenseChart = view.findViewById(R.id.expenseChart)

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

        txtIncomeSummary.text = if (incomeSummary.isNotEmpty()) {
            incomeSummary.entries.joinToString("\n") { "${it.key}: ${it.value}" }
        } else {
            "No income records"
        }

        txtExpenseSummary.text = if (expenseSummary.isNotEmpty()) {
            expenseSummary.entries.joinToString("\n") { "${it.key}: ${it.value}" }
        } else {
            "No expense records"
        }

        setPieChart(incomeChart, incomeSummary, "Income")
        setPieChart(expenseChart, expenseSummary, "Expenses")
    }

    private fun setPieChart(pieChart: PieChart, data: Map<String, Double>, centerText: String) {
        if (data.isEmpty()) {
            pieChart.clear()
            pieChart.centerText = "No data"
            return
        }

        val entries = data.map { PieEntry(it.value.toFloat(), it.key) }

        val dataSet = PieDataSet(entries, "").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextSize = 14f
            valueTextColor = Color.BLACK
        }

        pieChart.apply {
            this.data = PieData(dataSet)
            description.isEnabled = false
            this.centerText = "$centerText\nTotal: ${data.values.sum()}"
            setCenterTextColor(Color.DKGRAY)
            setEntryLabelColor(Color.BLACK)
            setUsePercentValues(false)
            animateY(800)
            invalidate()
        }
    }
}
