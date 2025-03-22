package com.financetracker.infrastructure.adapters.inbound.dto.request

import java.time.LocalDate
import java.util.*

data class AddTransactionRequest(
    val accountId: UUID,
    val amount: Double,
    val description: String,
    val categoryId: UUID,
    val occurredOn: LocalDate,
    val type: String
)
