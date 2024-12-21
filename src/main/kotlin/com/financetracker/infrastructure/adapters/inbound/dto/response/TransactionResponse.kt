package com.financetracker.infrastructure.adapters.inbound.dto.response

import com.financetracker.domain.model.Category
import java.time.LocalDate
import java.util.*

data class TransactionResponse(
    val id: UUID,
    val type: String,
    val category: Category,
    val description: String,
    var amount: Double = 0.0,
    val occurredOn: LocalDate,
    val shareable: Boolean,
    val account: UUID
)
