package com.financetracker.application.api.dto

import java.time.LocalDate
import java.util.*

data class CreditTransactionRequest(
    val account: UUID,
    val amount: Double,
    val description: String,
    val category: String,
    val occurredOn: LocalDate
)
