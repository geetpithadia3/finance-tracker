package com.financetracker.infrastructure.adapters.outbound.splitwise.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDate


data class SyncedTransaction(
    val id: Long,
    val amount: Double,
    val date: LocalDate,
    val description: String,
    val category: String,
    val type: String = "Debit"
)


@JsonIgnoreProperties(ignoreUnknown = true)
data class ExpenseResponse(val expenses: List<Expense>)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Expense(
    val id: Long,
    val group_id: Long?
)
