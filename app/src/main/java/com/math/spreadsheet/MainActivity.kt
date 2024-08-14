package com.math.spreadsheet

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.threetenabp.AndroidThreeTen
import com.math.expense.spreadsheet.R
import com.math.spreadsheet.helper.DatabaseHelper
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private var itemSelectedSpinner: Spinner? = null
    private var amountSelectedEditText: EditText? = null
    private var descriptionEditText: EditText? = null
    private var dateEditText: EditText? = null
    private lateinit var summaryTextView: TextView
    private lateinit var dbHelper: DatabaseHelper

    private fun init() {
        AndroidThreeTen.init(this)
        dbHelper = DatabaseHelper(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()

        setContentView(R.layout.activity_main)

        val btnAddExpense: Button = findViewById(R.id.btnAddExpense)
        val btnGoToSetup: Button = findViewById(R.id.btnGoToSetup)
        val btnGoToHistory: Button = findViewById(R.id.btnGoToExpenseHistory)

        itemSelectedSpinner = findViewById(R.id.expenseCategorySpinner)
        amountSelectedEditText = findViewById(R.id.editTextAmount)
        descriptionEditText = findViewById(R.id.editTextDescription)
        dateEditText = findViewById(R.id.editTextDate)
        summaryTextView = findViewById(R.id.summaryTextView)

        // Set the default date to the current date
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        dateEditText?.setText(currentDate.format(formatter))

        // Open date picker on dateEditText click
        dateEditText?.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
                    dateEditText?.setText(selectedDate.format(formatter))
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        btnAddExpense.setOnClickListener {
            clickAddExpense()
        }

        btnGoToSetup.setOnClickListener {
            val intent = Intent(this, SetupActivity::class.java)
            startActivity(intent)
        }

        btnGoToHistory.setOnClickListener {
            val intent = Intent(this, ExpensesTableActivity::class.java)
            startActivity(intent)
        }

        updateCurrentTotalBalance()
    }

    private fun clickAddExpense() {
        val selectedCategory = itemSelectedSpinner?.selectedItem.toString()
        val enteredAmount = amountSelectedEditText?.text.toString().toDoubleOrNull()
        val enteredDescription = descriptionEditText?.text.toString()
        val selectedDate = dateEditText?.text.toString()

        if (enteredAmount != null) {
            dbHelper.addExpense(
                selectedCategory,
                enteredAmount,
                enteredDescription,
                LocalDate.parse(selectedDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            )
            Toast.makeText(this, "Expense added!", Toast.LENGTH_SHORT).show()

            amountSelectedEditText?.text?.clear()
            descriptionEditText?.text?.clear()
            dateEditText?.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
        } else {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
        }

        updateCurrentTotalBalance()
    }

    private fun updateCurrentTotalBalance() {
        val availableMoney = dbHelper.getAvailableMoney()
        val totalExpenses = dbHelper.getTotalExpenses()

        "Summary: Available Money - Total Expenses = ${availableMoney?.minus(totalExpenses ?: 0.0) ?: "N/A"}"
            .let { summaryTextView.text = it }
    }

}