package com.math.spreadsheet

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.math.expense.spreadsheet.R
import com.math.spreadsheet.helper.DatabaseHelper

class ExpensesHistoryActivity : AppCompatActivity() {

    private lateinit var tableLayout: TableLayout
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val btnBack: Button = findViewById(R.id.btnBack)

        dbHelper = DatabaseHelper(this)
        tableLayout = findViewById(R.id.expensesTableLayout)

        val expenses = dbHelper.getAllExpenses()

        for (expense in expenses) {
            val tableRow = TableRow(this)

            val categoryTextView = TextView(this).apply {
                text = expense.category
                setPadding(8, 8, 8, 8)
                setTextColor(Color.BLACK)
            }

            val amountTextView = TextView(this).apply {
                text = expense.amount.toString()
                setPadding(8, 8, 8, 8)
                setTextColor(Color.BLACK)
            }

            val descriptionTextView = TextView(this).apply {
                text = expense.description
                setPadding(8, 8, 8, 8)
                setTextColor(Color.BLACK)
            }

            val dateTextView = TextView(this).apply {
                text = expense.monthYear
                setPadding(8, 8, 8, 8)
                setTextColor(Color.BLACK)
            }

            val btnEdit = Button(this).apply {
                text = "Edit"
                setOnClickListener {
                    val editIntent = Intent(this@ExpensesHistoryActivity, EditExpenseActivity::class.java)
                    editIntent.putExtra("expenseId", expense.id) // Pass the expense ID
                    startActivity(editIntent)
                }
            }

            val btnDelete = Button(this).apply {
                text = "Delete"
                setOnClickListener {
                    dbHelper.deleteExpense(expense.id)
                    recreate()
                }
            }

            tableRow.addView(categoryTextView)
            tableRow.addView(amountTextView)
            tableRow.addView(descriptionTextView)
            tableRow.addView(dateTextView)
            tableRow.addView(btnEdit)
            tableRow.addView(btnDelete)

            tableLayout.addView(tableRow)
        }

        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}
