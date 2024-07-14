package com.financetracker.infrastructure.adapters.outbound.splitwise.dto

import java.time.LocalDate

data class Expense(
    val amount: Double,
    val date: LocalDate,
    val description: String,
    val category: String,
    val type: String = "Debit"
)

data class SyncedTransaction(
    val id: Long,
    val amount: Double,
    val date: LocalDate,
    val description: String,
    val category: String,
    val type: String = "Debit"
)
