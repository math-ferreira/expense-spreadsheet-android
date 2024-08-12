package com.math.spreadsheet

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.math.expense.spreadsheet.R

class MainActivity : AppCompatActivity() {

    private var itemSelectedSpinner: Spinner? = null
    private var amountSelectedEditText: EditText? = null
    private var descriptionEditText: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnAddExpense: Button = findViewById(R.id.btnAddExpense)
        itemSelectedSpinner = findViewById(R.id.expenseCategorySpinner)
        amountSelectedEditText = findViewById(R.id.editTextAmount)
        descriptionEditText = findViewById(R.id.editTextDescription)

        btnAddExpense.setOnClickListener {
            clickAddExpense()
        }

    }

    private fun clickAddExpense() {
        // Retrieve the selected item from the Spinner
        val selectedCategory = itemSelectedSpinner?.selectedItem.toString()

        // Get the text entered in the EditText fields
        val enteredAmount = amountSelectedEditText?.text.toString()
        val enteredDescription = descriptionEditText?.text.toString()

        // Here you can perform any action with the retrieved values
        // For example, show them in a Toast message
        Toast.makeText(this, "Category: $selectedCategory\nAmount: $enteredAmount\nDescription: $enteredDescription", Toast.LENGTH_LONG).show()

        // You can also save these values to a database, send them to another activity, etc.
    }
}