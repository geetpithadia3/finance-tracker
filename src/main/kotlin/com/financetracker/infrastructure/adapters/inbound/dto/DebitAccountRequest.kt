package com.financetracker.infrastructure.adapters.inbound.dto

import java.time.LocalDate

data class DebitAccountRequest(
    val amount: Double,
    val description: String,
    val category: String,
    val occurredOn: LocalDate
)
