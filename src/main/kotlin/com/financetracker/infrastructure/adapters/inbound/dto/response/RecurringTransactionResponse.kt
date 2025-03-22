package com.financetracker.infrastructure.adapters.inbound.dto.response

import com.financetracker.domain.model.Category
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.DateFlexibility
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.RecurrenceFrequency
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.TransactionPriority
import java.time.LocalDate
import java.util.*

data class RecurringTransactionResponse(
    val id: UUID,
    val description: String,
    val amount: Double,
    val category: Category,
    val accountId: UUID,
    val frequency: RecurrenceFrequency,
    val startDate: LocalDate,
    val endDate: LocalDate? = null,
    val dateFlexibility: DateFlexibility = DateFlexibility.EXACT,
    val rangeStart: Int? = null,
    val rangeEnd: Int? = null,
    val preference: String? = null,
    val priority: TransactionPriority = TransactionPriority.MEDIUM,
    val isActive: Boolean = true,
    val lastMatchedTransactionId: UUID? = null,
    val isVariableAmount: Boolean = false,
    val estimatedMinAmount: Double? = null,
    val estimatedMaxAmount: Double? = null
) 