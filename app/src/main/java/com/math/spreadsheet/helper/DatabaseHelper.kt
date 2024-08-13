package com.math.spreadsheet.helper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.threeten.bp.LocalDate

// Database constants
private const val DATABASE_NAME = "expenses.db"
private const val DATABASE_VERSION = 1

// Table constants
private const val EXPENSES_TABLE_NAME = "expenses"
private const val SETUP_TABLE_NAME = "setup"
private const val COLUMN_ID = "id"
private const val COLUMN_YEAR = "year"
private const val COLUMN_MONTH = "month"
private const val COLUMN_CATEGORY = "category"
private const val COLUMN_AMOUNT = "amount"
private const val COLUMN_AVAILABLE_MONEY  = "available_money"
private const val COLUMN_DESCRIPTION = "description"
private const val CREATED_AT = "created_at"

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val SQL_CREATE_EXPENSE_TABLE = """
        CREATE TABLE $EXPENSES_TABLE_NAME (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_CATEGORY TEXT,
            $COLUMN_AMOUNT REAL,
            $COLUMN_DESCRIPTION TEXT,
            $CREATED_AT TEXT
        )
    """.trimIndent()

    private val SQL_CREATE_SETUP_TABLE = """
        CREATE TABLE $SETUP_TABLE_NAME (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_YEAR INTEGER,
            $COLUMN_MONTH INTEGER,
            $COLUMN_AVAILABLE_MONEY REAL
        )
    """.trimIndent()

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_SETUP_TABLE)
        db.execSQL(SQL_CREATE_EXPENSE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $EXPENSES_TABLE_NAME")
        onCreate(db)
    }

    fun addExpense(category: String, amount: Double, description: String, createdAt: LocalDate) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CATEGORY, category)
            put(COLUMN_AMOUNT, amount)
            put(COLUMN_DESCRIPTION, description)
            put(CREATED_AT, createdAt.toString())
        }
        db.insert(EXPENSES_TABLE_NAME, null, values)
        db.close()
    }

    fun saveOrUpdateAvailableMoney(amount: Double) {
        val db = this.writableDatabase

        val currentYear = LocalDate.now().year
        val currentMonth = LocalDate.now().monthValue

        val cursor = db.query(
            SETUP_TABLE_NAME,
            arrayOf(COLUMN_ID),
            "$COLUMN_YEAR = ? AND $COLUMN_MONTH = ?",
            arrayOf(currentYear.toString(), currentMonth.toString()),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            // Update existing record
            val contentValues = ContentValues().apply {
                put(COLUMN_AVAILABLE_MONEY, amount)
            }
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
            db.update(SETUP_TABLE_NAME, contentValues, "$COLUMN_ID = ?", arrayOf(id.toString()))
        } else {
            // Insert new record
            val contentValues = ContentValues().apply {
                put(COLUMN_YEAR, currentYear)
                put(COLUMN_MONTH, currentMonth)
                put(COLUMN_AVAILABLE_MONEY, amount)
            }
            db.insert(SETUP_TABLE_NAME, null, contentValues)
        }

        cursor.close()
        db.close()
    }

    fun getAvailableMoney(): Double? {
        val db = this.readableDatabase

        val currentYear = LocalDate.now().year
        val currentMonth = LocalDate.now().monthValue

        val cursor = db.query(
            SETUP_TABLE_NAME,
            arrayOf(COLUMN_AVAILABLE_MONEY),
            "$COLUMN_YEAR = ? AND $COLUMN_MONTH = ?",
            arrayOf(currentYear.toString(), currentMonth.toString()),
            null, null, null
        )

        var availableMoney: Double? = null
        if (cursor.moveToFirst()) {
            availableMoney = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AVAILABLE_MONEY))
        }

        cursor.close()
        db.close()
        return availableMoney
    }

}