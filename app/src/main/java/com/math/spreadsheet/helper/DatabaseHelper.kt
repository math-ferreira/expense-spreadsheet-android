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
private const val TABLE_NAME = "expenses"
private const val COLUMN_ID = "id"
private const val COLUMN_CATEGORY = "category"
private const val COLUMN_AMOUNT = "amount"
private const val COLUMN_DESCRIPTION = "description"
private const val CREATED_AT = "created_at"

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_CATEGORY TEXT," +
                "$COLUMN_AMOUNT REAL," +
                "$COLUMN_DESCRIPTION TEXT," +
                "$CREATED_AT TEXT)"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
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
        db.insert(TABLE_NAME, null, values)
        db.close()
    }
}