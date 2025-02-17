package com.financetracker.domain.model

import java.time.LocalDate
import java.util.*

data class Transaction(
    val id: UUID? = null,
    val type: TransactionType? = null,
    val category: Category? = null,
    val description: String? = null,
    val amount: Double = 0.0,
    val occurredOn: LocalDate? = null,
    val accountId: UUID,
    val isDeleted: Boolean = false,
    val linkedTransaction: Transaction? = null,
    val refunded: Boolean = false,
    val personalShare: Double = 0.0,
    val owedShare: Double = 0.0,
    val shareMetadata: String? = null
)
