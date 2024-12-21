package com.financetracker.infrastructure.adapters.inbound.dto.request

import com.financetracker.domain.model.Category
import java.time.LocalDate
import java.util.*

data class UpdateTransactionRequest(
    val id: UUID,
    val description: String,
    val amount: Double,
    val categoryId: UUID,
    val occurredOn: LocalDate,
    val deleted: Boolean,
    val account: UUID,
)

data class UpdateTransactionSharesRequest(
    val id: UUID,
    val description: String,
    val amount: Double,
    val category: Category,
    val occurredOn: LocalDate,
    val account: UUID,
    val splitShares: List<SplitShare>
)

data class SplitShare(val userId: String, var paidShare: Double, val owedShare: Double)
