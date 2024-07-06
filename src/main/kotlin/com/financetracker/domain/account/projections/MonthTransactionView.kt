package com.financetracker.domain.account.projections

import com.financetracker.domain.account.model.Category
import com.financetracker.domain.account.model.TransactionType
import java.time.LocalDate

data class MonthTransactionsView(
    val id: String,
    val account: String,
    val amount: Double,
    val description: String,
    val category: Category,
    val type: TransactionType,
    val date: LocalDate
)
