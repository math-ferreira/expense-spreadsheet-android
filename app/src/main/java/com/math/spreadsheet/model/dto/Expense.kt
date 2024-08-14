package com.math.spreadsheet.model.dto

data class Expense(
    val category: String,
    val amount: Double,
    val description: String?,
    val createdAt: String
)
