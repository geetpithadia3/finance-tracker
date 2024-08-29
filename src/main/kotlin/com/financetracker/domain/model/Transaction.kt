package com.financetracker.domain.model

import com.financetracker.domain.account.model.TransactionType
import java.time.LocalDate
import java.time.LocalDateTime

data class Transaction(
    val id: Long,
    val type: TransactionType,
    val category: Category,
    val description: String,
    var amount: Double = 0.0,
    val occurredOn: LocalDate,
    var deleted: Boolean = false,
    var lastSyncedAt: LocalDateTime,
    var externalId: String? = null,
    val account: String
)
