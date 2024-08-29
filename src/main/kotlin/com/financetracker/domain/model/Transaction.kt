package com.financetracker.domain.model

import com.financetracker.domain.account.model.Category
import com.financetracker.domain.account.model.TransactionType
import java.time.LocalDate
import java.time.LocalDateTime

data class Transaction(
    val id: Long,
    val type: TransactionType,
    val category: Category,
    val description: String,
    var amount: Double = 0.0,
    var externalId: String? = null,
    val occurredOn: LocalDate,
    var lastSyncedAt: LocalDateTime,
    val account: String
)
