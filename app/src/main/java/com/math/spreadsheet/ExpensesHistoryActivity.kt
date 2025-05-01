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
import com.math.spreadsheet.util.ToastUtil
import com.math.spreadsheet.util.roundTo2DecimalPlaces
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

        val expenses = dbHelper.getAllExpenses(true)

        for (expense in expenses) {
            val tableRow = TableRow(this)

            val categoryTextView = TextView(this).apply {
                text = expense.category
                setPadding(8, 8, 8, 8)
                setTextColor(Color.BLACK)
            }

            val amountTextView = TextView(this).apply {
                text = "€ ${expense.amount.roundTo2DecimalPlaces()}"
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
                    val editIntent =
                        Intent(this@ExpensesHistoryActivity, EditExpenseActivity::class.java)
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

            tableRow.addView(dateTextView)
            tableRow.addView(categoryTextView)
            tableRow.addView(amountTextView)
            tableRow.addView(descriptionTextView)
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
        }


        btnExport.setOnClickListener {
            val exportOptions = arrayOf("Android app", "Spreadsheet document")

            AlertDialog.Builder(this)
                .setTitle("Choose export format")
                .setItems(exportOptions) { _, which ->
                    showFilterDialog { monthYearFilter ->
                        val allExpenses = dbHelper.getAllExpenses()
                        val expensesList = if (!monthYearFilter.isNullOrEmpty()) {
                            allExpenses.filter {
                                it.monthYear.equals(monthYearFilter, ignoreCase = true)
                            }
                        } else {
                            allExpenses
                        }

                        when (which) {
                            0 -> {
                                val csvData = convertExpensesToCsv(expensesList)
                                showCopyPasteDialogue(csvData)
                            }

                            1 -> {
                                val sheetData = convertExpensesToSpreadsheet(expensesList)
                                showCopyPasteDialogue(sheetData)
                            }
                        }
                    }
                }
                .show()
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
                dbHelper.addExpense(
                    expense.category,
                    expense.amount,
                    expense.description ?: "",
                    LocalDate.now()
                )
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
            if (columns.size == 4) {
                val category = columns[0].trim()
                val amount = columns[1].trim().toDoubleOrNull() ?: 0.0
                val description = columns[2].trim()
                val monthYear = columns[3].trim()

                val expense = Expense(
                    category = category,
                    amount = amount,
                    description = description,
                    monthYear = monthYear
                )
                expenses.add(expense)
            }
        }

        return expenses
    }


    private fun convertExpensesToCsv(expenses: List<Expense>): String {
        val stringBuilder = StringBuilder()
        for (expense in expenses) {
            stringBuilder.append("${expense.category},${expense.amount},${expense.description},${expense.monthYear};\n")
        }
        return stringBuilder.toString()
    }

    private fun showCopyPasteDialogue(expensiveData: String) {
        val textView = TextView(this).apply {
            text = expensiveData
            setPadding(32, 32, 32, 32)
            setTextIsSelectable(true)
        }

        AlertDialog.Builder(this)
            .setTitle("Exported Expenses")
            .setView(textView)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setNeutralButton("Copy") { dialog, _ ->
                val clipboard =
                    getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = android.content.ClipData.newPlainText("Exported Expenses", expensiveData)
                clipboard.setPrimaryClip(clip)
                ToastUtil.showToast(this, "Copied to clipboard")
            }
            .show()
    }


    private fun convertExpensesToSpreadsheet(expenses: List<Expense>): String {
        val stringBuilder = StringBuilder()

        expenses.forEachIndexed { index, expense ->
            stringBuilder.append("${index + 1}\t${expense.category}\t${expense.amount.roundTo2DecimalPlaces()}\t${expense.description}\t${expense.createdAt}\n")
        }

        return stringBuilder.toString()
    }

    private fun showFilterDialog(onFilterChosen: (String?) -> Unit) {
        val layout = layoutInflater.inflate(R.layout.dialog_month_year_filter, null)
        val monthInput = layout.findViewById<EditText>(R.id.editMonth)
        val yearInput = layout.findViewById<EditText>(R.id.editYear)

        AlertDialog.Builder(this)
            .setTitle("Filter by Month and Year")
            .setView(layout)
            .setPositiveButton("Filter") { _, _ ->
                val month = monthInput.text.toString()
                val year = yearInput.text.toString()
                if (month.isNotBlank() && year.isNotBlank()) {
                    onFilterChosen("$month/$year")
                } else {
                    onFilterChosen(null) // Export everything
                }
            }
            .setNegativeButton("All") { _, _ ->
                onFilterChosen(null) // Export everything
            }
            .show()
    }


}
