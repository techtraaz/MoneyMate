package com.example.moneymate

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class Activity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity2)

        val pieChart = findViewById<PieChart>(R.id.pieChart)

        val entries = listOf(
            PieEntry(40f, "Apples"),
            PieEntry(30f, "Bananas"),
            PieEntry(20f, "Cherries"),
            PieEntry(10f, "Grapes")
        )

        val dataSet = PieDataSet(entries, "Fruits")
        dataSet.colors = listOf(
            Color.RED, Color.YELLOW, Color.MAGENTA, Color.GREEN
        )
        dataSet.valueTextSize = 14f

        val pieData = PieData(dataSet)

        pieChart.data = pieData
        pieChart.description.isEnabled = false
        pieChart.centerText = "Fruit Share"
        pieChart.setUsePercentValues(true)
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.animateY(1000)

        val legend = pieChart.legend
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.isWordWrapEnabled = true

        pieChart.invalidate() // refresh
    }
}
