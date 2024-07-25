package com.financetracker.infrastructure.adapters.inbound.dto

import java.time.LocalDate

data class AddTransactionRequest(
    val accountId: String,
    val amount: Double,
    val description: String,
    val category: String,
    val occurredOn: LocalDate,
    val toAccount: String? = null,
    val type: String
)
