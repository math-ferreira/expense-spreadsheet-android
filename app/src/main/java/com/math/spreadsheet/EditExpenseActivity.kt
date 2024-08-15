package com.math.spreadsheet

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.math.expense.spreadsheet.R
import com.math.spreadsheet.helper.DatabaseHelper

class EditExpenseActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var expenseId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_expense)

        dbHelper = DatabaseHelper(this)

        val categorySpinner: Spinner = findViewById(R.id.expenseCategorySpinner)
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
                dateEditText.setText(expense.createdAt)
            }
        }

        btnUpdate.setOnClickListener {
            val category = categorySpinner.selectedItem.toString()
            val amount = amountEditText.text.toString().toDouble()
            val description = descriptionEditText.text.toString()
            val date = dateEditText.text.toString()

            dbHelper.updateExpense(expenseId, category, amount, description, date)
            finish()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }
}
