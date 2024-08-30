package com.financetracker.domain.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class Transaction(
    val id: UUID? = null,
    val type: TransactionType,
    val category: Category,
    val description: String,
    var amount: Double = 0.0,
    var externalId: String? = null,
    val occurredOn: LocalDate,
    var lastSyncedAt: LocalDateTime,
    val accountId: UUID
)
