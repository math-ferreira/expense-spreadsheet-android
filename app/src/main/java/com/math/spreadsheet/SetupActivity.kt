package com.math.spreadsheet

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.math.expense.spreadsheet.R
import com.math.spreadsheet.helper.DatabaseHelper

class SetupActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var availableMoneyEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        dbHelper = DatabaseHelper(this)

        availableMoneyEditText = findViewById(R.id.editTextAvailableMoney)
        val btnSave: Button = findViewById(R.id.btnSave)

        btnSave.setOnClickListener {
            saveAvailableMoney()
        }

        // Load existing value if available
        loadAvailableMoney()
    }

    private fun saveAvailableMoney() {
        val enteredAmount = availableMoneyEditText.text.toString().toDoubleOrNull()

        if (enteredAmount != null) {
            dbHelper.saveOrUpdateAvailableMoney(enteredAmount)
            Toast.makeText(this, "Available money for this month saved!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadAvailableMoney() {
        val availableMoney = dbHelper.getAvailableMoney()
        availableMoney?.let {
            availableMoneyEditText.setText(it.toString())
        }
    }
}
