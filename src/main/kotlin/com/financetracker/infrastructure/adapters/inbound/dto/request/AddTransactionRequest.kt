package com.financetracker.infrastructure.adapters.inbound.dto.request

import java.time.LocalDate
import java.util.*

data class AddTransactionRequest(
    val accountId: UUID,
    val amount: Double,
    val description: String,
    val category: String,
    val occurredOn: LocalDate,
    val toAccount: String? = null,
    val type: String
)
