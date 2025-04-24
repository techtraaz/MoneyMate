package com.example.moneymate

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.*

class BudgetFragment : Fragment() {

    private lateinit var txtMonthYear: TextView
    private lateinit var btnPrev: ImageView
    private lateinit var btnNext: ImageView
    private lateinit var monthyear: TextView
    private lateinit var budgetMonth: TextView
    private lateinit var addbudget: TextView
    private lateinit var btnsave: Button
    private lateinit var budgetPercentage: TextView
    private lateinit var pieChart: PieChart

    private lateinit var storage: TransactionStorage
    private var calendar = Calendar.getInstance()

    private val CHANNEL_ID = "budget_alert_channel"
    private val NOTIFICATION_ID = 999

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_budget, container, false)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtMonthYear = view.findViewById(R.id.txtMonthYear)
        btnPrev = view.findViewById(R.id.btnPrev)
        btnNext = view.findViewById(R.id.btnNext)
        monthyear = view.findViewById(R.id.monthyear)
        btnsave = view.findViewById(R.id.btnSetBudget)
        budgetMonth = view.findViewById(R.id.budgetMonth)
        addbudget = view.findViewById(R.id.edtBudget)
        budgetPercentage = view.findViewById(R.id.budgetPercentage)
        pieChart = view.findViewById(R.id.pieChart)

        storage = TransactionStorage(requireContext())
        createNotificationChannel()
        updateUI()

        btnsave.setOnClickListener {
            saveBudget()
        }

        btnPrev.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateUI()
        }

        btnNext.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateUI()
        }
    }

    private fun getCurrentMonthYear(): String {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        return sdf.format(calendar.time)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun updateUI() {
        val monthYear = getCurrentMonthYear()
        txtMonthYear.text = monthYear

        val totalExpense = storage.getTotalExpenseForMonth(monthYear)
        val budget = storage.getBudgetForMonth(monthYear)

        monthyear.text = totalExpense.toString()

        if (budget > 0) {
            budgetMonth.text = budget.toString()
        } else {
            budgetMonth.text = "Budget Not Set"
        }

        updateBudgetPercentage(totalExpense, budget)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun updateBudgetPercentage(totalExpense: Double, budget: Double) {
        if (budget > 0) {
            val percentage = (totalExpense / budget) * 100.0
            val rounded = String.format("%.2f", percentage)
            budgetPercentage.text = "$rounded%"

            if (percentage > 100.0) {
                showBudgetExceededNotification()
            }

            updatePieChart(percentage)
        } else {
            budgetPercentage.text = "Budget Not Set"
            pieChart.clear()
            pieChart.setNoDataText("No budget set")
        }
    }

    private fun updatePieChart(percentage: Double) {
        val used = percentage.coerceAtMost(100.0).toFloat()
        val remaining = (100 - used).coerceAtLeast(0f)

        val entries = listOf(
            PieEntry(used, "Used"),
            PieEntry(remaining, "Remaining")
        )

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(
                ColorTemplate.MATERIAL_COLORS[0], // red-ish
                ColorTemplate.MATERIAL_COLORS[1]  // green-ish
            )
            valueTextColor = android.graphics.Color.WHITE
            valueTextSize = 14f
        }

        pieChart.apply {
            data = PieData(dataSet)
            description.isEnabled = false
            isRotationEnabled = false
            legend.isEnabled = false
            setUsePercentValues(true)
            setEntryLabelColor(android.graphics.Color.BLACK)
            invalidate()
        }
    }

    private fun saveBudget() {
        val budgetInput = addbudget.text.toString()
        val budgetAmount = budgetInput.toDoubleOrNull()

        if (budgetAmount != null && budgetAmount > 0) {
            val monthYear = getCurrentMonthYear()
            storage.setBudgetForMonth(monthYear, budgetAmount)
            Toast.makeText(requireContext(), "Budget set for $monthYear", Toast.LENGTH_SHORT).show()
            updateUI()
        } else {
            Toast.makeText(requireContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showBudgetExceededNotification() {
        val notification = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Budget Exceeded!")
            .setContentText("Your expenses have exceeded your budget.")
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
            val notificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
