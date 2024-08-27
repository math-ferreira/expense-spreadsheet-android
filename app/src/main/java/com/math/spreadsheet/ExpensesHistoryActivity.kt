package com.math.spreadsheet

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.math.expense.spreadsheet.R
import com.math.spreadsheet.helper.DatabaseHelper
import com.math.spreadsheet.model.dto.Expense
import org.threeten.bp.LocalDate

class ExpensesHistoryActivity : AppCompatActivity() {

    private lateinit var tableLayout: TableLayout
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val btnBack: Button = findViewById(R.id.btnBack)
        val btnImport: Button = findViewById(R.id.btnImport)
        val btnExport: Button = findViewById(R.id.btnExport)

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
                    dbHelper.deleteExpense(expense.id!!)
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

        btnImport.setOnClickListener {
            showImportDialog()
            //finish()
        }

        btnExport.setOnClickListener {
            val expensesList = dbHelper.getAllExpenses()
            val csvData = convertExpensesToCsv(expensesList)
            showCsvDialog(csvData)
        }

    }

    private fun showImportDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Import CSV Expenses")

        val editText = EditText(this).apply {
            hint = "Paste CSV data here"
            setPadding(32, 32, 32, 32)
        }

        builder.setView(editText)
        builder.setPositiveButton("Import") { dialog, _ ->
            val csvData = editText.text.toString()
            importCsvData(csvData)
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun importCsvData(csvData: String) {
        val expenses = parseCsvToExpenses(csvData)

        if (expenses.isNotEmpty()) {
            for (expense in expenses) {
                dbHelper.addExpense(expense.category, expense.amount, expense.description ?: "", LocalDate.now())
            }
            recreate()
        } else {
            AlertDialog.Builder(this)
                .setTitle("Import Failed")
                .setMessage("No valid data found in the CSV.")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    private fun parseCsvToExpenses(csvData: String): List<Expense> {
        val expenses = mutableListOf<Expense>()
        val rows = csvData.split(";\n")

        for (row in rows) {
            val columns = row.split(",")
            if (columns.size == 4) { // Ensure there are exactly 4 columns
                val category = columns[0].trim()
                val amount = columns[1].trim().toDoubleOrNull() ?: 0.0
                val description = columns[2].trim()
                val monthYear = columns[3].trim()

                // Create an Expense object and add it to the list
                val expense = Expense(category = category, amount = amount, description = description, monthYear = monthYear)
                expenses.add(expense)
            }
        }

        return expenses
    }


    private fun convertExpensesToCsv(expenses: List<Expense>): String {
        val stringBuilder = StringBuilder()
        for (expense in expenses) {
            // Appending each expense as a CSV row
            stringBuilder.append("${expense.category},${expense.amount},${expense.description},${expense.monthYear};\n")
        }
        return stringBuilder.toString()
    }

    private fun showCsvDialog(csvData: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Exported CSV Expenses")

        val textView = TextView(this).apply {
            text = csvData
            setPadding(32, 32, 32, 32)
            setTextIsSelectable(true)
        }

        builder.setView(textView)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }


}
