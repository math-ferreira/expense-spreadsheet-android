package com.math.spreadsheet.model.dto

data class Expense(
    val id: Long? = null,
    val category: String,
    val amount: Double,
    val description: String?,
    val monthYear: String
)
