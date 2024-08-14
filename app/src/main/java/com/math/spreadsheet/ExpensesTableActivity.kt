package com.math.spreadsheet

import android.os.Bundle
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.math.expense.spreadsheet.R
import com.math.spreadsheet.helper.DatabaseHelper

class ExpensesTableActivity : AppCompatActivity() {

    private lateinit var tableLayout: TableLayout
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        dbHelper = DatabaseHelper(this)

        tableLayout = findViewById(R.id.expensesTableLayout)

        // Load the expenses from the database
        val expenses = dbHelper.getAllExpenses()

        // Add each expense as a new row in the table
        for (expense in expenses) {
            val tableRow = TableRow(this)

            val categoryTextView = TextView(this)
            categoryTextView.text = expense.category
            categoryTextView.setPadding(8, 8, 8, 8)

            val amountTextView = TextView(this)
            amountTextView.text = expense.amount.toString()
            amountTextView.setPadding(8, 8, 8, 8)

            val descriptionTextView = TextView(this)
            descriptionTextView.text = expense.description
            descriptionTextView.setPadding(8, 8, 8, 8)

            val dateTextView = TextView(this)
            dateTextView.text = expense.createdAt
            dateTextView.setPadding(8, 8, 8, 8)

            tableRow.addView(categoryTextView)
            tableRow.addView(amountTextView)
            tableRow.addView(descriptionTextView)
            tableRow.addView(dateTextView)

            tableLayout.addView(tableRow)
        }
    }
}
