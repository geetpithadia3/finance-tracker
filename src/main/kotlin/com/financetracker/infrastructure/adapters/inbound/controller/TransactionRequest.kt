package com.financetracker.infrastructure.adapters.inbound.controller

import java.time.LocalDate

data class TransactionRequest(
    val amount: Double,
    val description: String,
    val category: String,
    val occurredOn: LocalDate
)
