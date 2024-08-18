package com.math.spreadsheet

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.math.expense.spreadsheet.R
import com.math.spreadsheet.helper.DatabaseHelper
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.Calendar

class EditExpenseActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var expenseId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_expense)

        dbHelper = DatabaseHelper(this)

        val categorySpinner: Spinner = findViewById(R.id.expenseCategorySpinner)
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.expense_categories,
            R.layout.spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        val amountEditText: EditText = findViewById(R.id.editTextAmount)
        val descriptionEditText: EditText = findViewById(R.id.editTextDescription)
        val dateEditText: EditText = findViewById(R.id.editTextDate)
        val btnUpdate: Button = findViewById(R.id.btnUpdateExpense)
        val btnCancel: Button = findViewById(R.id.btnCancel)

        expenseId = intent.getLongExtra("expenseId", -1)

        if (expenseId != -1L) {
            val expense = dbHelper.getExpenseById(expenseId)

            if (expense != null) {
                val categories = resources.getStringArray(R.array.expense_categories)
                val categoryPosition = categories.indexOf(expense.category)
                categorySpinner.setSelection(categoryPosition)

                amountEditText.setText(expense.amount.toString())
                descriptionEditText.setText(expense.description)
                dateEditText.setText(expense.monthYear)
            }
        }

        btnUpdate.setOnClickListener {
            val category = categorySpinner.selectedItem.toString()
            val amount = amountEditText.text.toString().toDouble()
            val description = descriptionEditText.text.toString()
            val date = dateEditText.text.toString()

            dbHelper.updateExpense(expenseId, category, amount, description, date)
            val intent = Intent(this, ExpensesHistoryActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        // Open date picker on dateEditText click
        dateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
                    dateEditText.setText(selectedDate.format(formatter))
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }
}
