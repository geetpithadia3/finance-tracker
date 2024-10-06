package com.financetracker.infrastructure.adapters.inbound.dto.request

import java.time.LocalDate
import java.util.*

data class UpdateTransactionRequest(
    val id: UUID,
    val description: String,
    val category: String,
    val occurredOn: LocalDate,
    val deleted: Boolean,
    val account: UUID
)
