package com.financetracker.domain.account.valueObjects

import com.financetracker.domain.account.model.Category
import java.time.LocalDate

data class TransactionDetails(
    val description: String,
    val category: Category,
    val occurredOn: LocalDate
)
