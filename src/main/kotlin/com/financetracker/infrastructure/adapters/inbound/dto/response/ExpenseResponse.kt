package com.financetracker.infrastructure.adapters.inbound.dto.response

import java.time.LocalDate
import java.util.*

data class ExpenseResponse(
    val id: UUID,
    val type: String,
    val category: String,
    val description: String,
    var amount: Double = 0.0,
    val occurredOn: LocalDate,
    val personalShare: Double = amount,
    val account: UUID
)
