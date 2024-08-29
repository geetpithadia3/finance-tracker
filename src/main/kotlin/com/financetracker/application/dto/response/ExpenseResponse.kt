package com.financetracker.application.dto.response

import com.financetracker.domain.account.model.Category
import com.financetracker.domain.account.model.TransactionType
import java.time.LocalDate

data class ExpenseResponse(
    val id: Long,
    val type: TransactionType,
    val category: Category,
    val description: String,
    var amount: Double = 0.0,
    val occurredOn: LocalDate,
    val account: String
)
