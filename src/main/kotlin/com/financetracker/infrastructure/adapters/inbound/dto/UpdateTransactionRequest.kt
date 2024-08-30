package com.financetracker.infrastructure.adapters.inbound.dto

import java.time.LocalDate

data class UpdateTransactionRequest(
    val id: String,
    val account: String,
    val amount: Double,
    val description: String,
    val category: String,
    val occurredOn: LocalDate,
    val deleted: Boolean
)
