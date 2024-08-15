package com.math.spreadsheet

import android.content.Intent
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
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        // Load existing value if available
        loadAvailableMoney()
    }

    private fun saveAvailableMoney() {
        val enteredAmount = availableMoneyEditText.text.toString().toDoubleOrNull()

        if (enteredAmount != null) {
            dbHelper.saveOrUpdateAvailableMoney(enteredAmount)
            Toast.makeText(this, "Available money for this month has been updated!", Toast.LENGTH_LONG).show()
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
