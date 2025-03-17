package com.financetracker.infrastructure.adapters.inbound.dto.response

import com.financetracker.domain.model.Category
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.RecurrenceFrequency
import java.time.LocalDate
import java.util.*
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.DateFlexibility
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.TransactionPriority

data class TransactionResponse(
    val id: UUID,
    val type: String,
    val category: Category,
    val description: String,
    var amount: Double = 0.0,
    val occurredOn: LocalDate,
    val account: UUID,
    val refunded: Boolean,
    val personalShare: Double = 0.0,
    val owedShare: Double = 0.0,
    val shareMetadata: String? = null,
    val recurrence: RecurrenceResponse? = null
)

data class RecurrenceResponse(
    val id: UUID,
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
