package com.financetracker.infrastructure.adapters.inbound.dto.request

import com.financetracker.infrastructure.adapters.outbound.persistence.entity.DateFlexibility
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.RecurrenceFrequency
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.TransactionPriority
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
    val refunded: Boolean,
    val personalShare: Double = 0.0,
    val owedShare: Double = 0.0,
    val shareMetadata: String? = null,
    val recurrence: RecurrenceRequest? = null
)

data class RecurrenceRequest(
    val id: UUID? = null,
    val frequency: RecurrenceFrequency,
    val startDate: LocalDate,
    val endDate: LocalDate? = null,
    val dateFlexibility: DateFlexibility = DateFlexibility.EXACT,
    val rangeStart: Int? = null,
    val rangeEnd: Int? = null,
    val preference: String? = null,
    val priority: TransactionPriority = TransactionPriority.MEDIUM,
    val isVariableAmount: Boolean = false,
    val estimatedMinAmount: Double? = null,
    val estimatedMaxAmount: Double? = null
)
