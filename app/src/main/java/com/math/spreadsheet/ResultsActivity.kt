package com.math.spreadsheet

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.math.expense.spreadsheet.R
import com.math.spreadsheet.helper.DatabaseHelper
import org.threeten.bp.LocalDateTime

class ResultsActivity : AppCompatActivity() {

    private lateinit var pieChart: PieChart
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var tableLayout: TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)
        dbHelper = DatabaseHelper(this)
        pieChart = findViewById(R.id.pieChart)

        val now = LocalDateTime.now()
        var expensesByCategory = dbHelper.getExpensesByCategory(now.monthValue, now.year)
        var totalExpenses = dbHelper.getTotalExpenses(now.monthValue, now.year)

        val btnBack: Button = findViewById(R.id.btnBack)
        tableLayout = findViewById(R.id.tableLayout)

        setupPieChart(expensesByCategory, totalExpenses)
        addTableRows()

        btnBack.setOnClickListener {
            finish()
        }

    }

    private fun setupPieChart(expensesByCategory: Map<String, Float>, totalExpenses: Double?) {
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)
        pieChart.dragDecelerationFrictionCoef = 0.95f
        pieChart.setDrawHoleEnabled(true)
        pieChart.setHoleColor(Color.WHITE)
        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)
        pieChart.holeRadius = 58f
        pieChart.setTransparentCircleRadius(61f)
        pieChart.setDrawCenterText(true)
        pieChart.rotationAngle = 0f
        pieChart.isRotationEnabled = true
        pieChart.setHighlightPerTapEnabled(true)
        pieChart.animateY(1400, Easing.EaseInOutQuad)
        pieChart.legend.isEnabled = false
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.setEntryLabelTextSize(12f)

        val entries: ArrayList<PieEntry> = ArrayList()
        val colors: ArrayList<Int> = ArrayList()

        expensesByCategory.forEach { (category, amount) ->
            entries.add(
                PieEntry(
                    (((amount * 100) / (totalExpenses ?: 1.0)).toFloat()),
                    "$category ($$amount)"
                )
            )
        }

        val dataSet = PieDataSet(entries, "Percentage of each expense")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        colors.add(resources.getColor(R.color.purple_200))
        colors.add(resources.getColor(R.color.yellow))
        colors.add(resources.getColor(R.color.red))
        colors.add(resources.getColor(R.color.green))
        colors.add(resources.getColor(R.color.textBlue))
        colors.add(resources.getColor(R.color.purple_700))
        colors.add(resources.getColor(R.color.teal_700))

        dataSet.colors = colors
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(20f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)
        data.setValueTextColor(Color.WHITE)
        pieChart.data = data
        pieChart.highlightValues(null)
        pieChart.invalidate()
    }

    private fun addTableRows() {

        val allExpenses = dbHelper.getAllExpenses()

        val distinctMonthYearList = allExpenses
            .distinctBy { it.monthYear }
            .map { it.monthYear }

        val categories = listOf(
            "Supermarket",
            "Restaurant",
            "Clothes",
            "Bills",
            "Entertainment",
            "Transport",
            "Health",
            "Other"
        )

        distinctMonthYearList.forEach { monthYearList ->

            val row = TableRow(this).apply {
                setPadding(4, 4, 4, 4)
            }

            row.addView(createTextView(monthYearList))

            categories.forEach { category ->
                val expensesByCategory = allExpenses
                    .filter { it.category == category && it.monthYear == monthYearList}
                    .sumOf { it.amount }

                row.addView(createTextView(expensesByCategory.toString()))
            }
            tableLayout.addView(row)
        }
    }

    private fun createTextView(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            setPadding(8, 8, 8, 8)
            setTextColor(Color.BLACK)
            setBackgroundResource(R.color.grey)  // Optional: to give a background color
        }
    }

}