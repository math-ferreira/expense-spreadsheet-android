package com.math.spreadsheet.helper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.math.spreadsheet.model.dto.Expense
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

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
private const val COLUMN_AVAILABLE_MONEY = "available_money"
private const val COLUMN_DESCRIPTION = "description"
private const val COLUMN_CREATED_AT = "created_at"

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val SQL_CREATE_EXPENSE_TABLE = """
        CREATE TABLE $EXPENSES_TABLE_NAME (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_CATEGORY TEXT,
            $COLUMN_AMOUNT REAL,
            $COLUMN_DESCRIPTION TEXT,
            $COLUMN_YEAR INTEGER,
            $COLUMN_MONTH INTEGER,
            $COLUMN_CREATED_AT TEXT
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
            put(COLUMN_MONTH, createdAt.monthValue)
            put(COLUMN_YEAR, createdAt.year)
            put(COLUMN_CREATED_AT, createdAt.toString())
        }
        db.insert(EXPENSES_TABLE_NAME, null, values)
        db.close()
    }

    fun updateExpense(expenseId: Long, category: String, amount: Double, description: String, date: String) {

        val localDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE)

        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CATEGORY, category)
            put(COLUMN_AMOUNT, amount)
            put(COLUMN_DESCRIPTION, description)
            put(COLUMN_CREATED_AT, date)
            put(COLUMN_MONTH, localDate.monthValue)
            put(COLUMN_YEAR, localDate.year)
        }
        db.update(EXPENSES_TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(expenseId.toString()))
        db.close()
    }

    fun getExpenseById(expenseId: Long): Expense? {
        val db = this.readableDatabase
        val cursor = db.query(
            EXPENSES_TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_CATEGORY, COLUMN_AMOUNT, COLUMN_DESCRIPTION, COLUMN_CREATED_AT),
            "$COLUMN_ID = ?",
            arrayOf(expenseId.toString()),
            null,
            null,
            null
        )

        var expense: Expense? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
            val amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
            val createdAt = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT))

            expense = Expense(id, category, amount, description, createdAt)
        }
        cursor.close()
        db.close()

        return expense
    }


    fun deleteExpense(expenseId: Long) {
        val db = this.writableDatabase
        db.delete(EXPENSES_TABLE_NAME, "$COLUMN_ID = ?", arrayOf(expenseId.toString()))
        db.close()
    }

    fun getAllExpenses(): List<Expense> {
        val expenses = mutableListOf<Expense>()
        val db = this.readableDatabase

        val cursor = db.query(
            EXPENSES_TABLE_NAME, // Corrected table name
            arrayOf(COLUMN_ID, COLUMN_CATEGORY, COLUMN_AMOUNT, COLUMN_DESCRIPTION, COLUMN_MONTH, COLUMN_YEAR),
            null,
            null,
            null, null, null
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
                val amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                val month = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MONTH))
                val year = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_YEAR))

                val expense = Expense(id, category, amount, description, "$month/$year")
                expenses.add(expense)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return expenses
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

    fun getTotalExpenses(month: Int, year: Int): Double? {
        val db = this.readableDatabase

        val cursor = db.query(
            EXPENSES_TABLE_NAME,
            arrayOf(COLUMN_AMOUNT),
            "$COLUMN_YEAR = ? AND $COLUMN_MONTH = ?",
            arrayOf(year.toString(), month.toString()),
            null, null, null
        )

        var totalExpenses: Double = 0.0
        while (cursor.moveToNext()) {
            cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT))
                .let { totalExpenses += it }
        }

        cursor.close()
        db.close()
        return totalExpenses
    }

    fun getExpensesByCategory(month: Int, year: Int): Map<String, Float> {
        val db = this.readableDatabase
        val query = """
        SELECT $COLUMN_CATEGORY, SUM($COLUMN_AMOUNT) as total 
        FROM $EXPENSES_TABLE_NAME 
        WHERE $COLUMN_YEAR = ? AND $COLUMN_MONTH = ? 
        GROUP BY $COLUMN_CATEGORY
    """
        val cursor = db.rawQuery(query, arrayOf(year.toString(), month.toString()))

        val categoryExpenses = mutableMapOf<String, Float>()

        if (cursor.moveToFirst()) {
            do {
                val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
                val total = cursor.getFloat(cursor.getColumnIndexOrThrow("total"))
                categoryExpenses[category] = total
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return categoryExpenses
    }



}