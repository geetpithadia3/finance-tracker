package com.financetracker.infrastructure.adapters.inbound.dto.response

import java.time.LocalDate
import java.util.*

// Root response - simplified to just return list of paychecks with allocations
data class AllocationResponse(val paychecks: List<PaycheckAllocation>)

// Combined model with paycheck details and allocation information
data class PaycheckAllocation(
    val id: UUID,
    val amount: Double,
    val date: LocalDate,
    val source: String,
    val frequency: String,
    val expenses: List<UpcomingExpense>,
    val totalAllocationAmount: Double,
    val remainingAmount: Double,
    val nextPaycheckDate: LocalDate?
)

// Expense model with confidence for variable expenses
data class UpcomingExpense(
    val id: UUID,
    val description: String,
    val amount: Double,
    val dueDate: LocalDate,
    val category: String,
    val isRecurring: Boolean,
    val variabilityFactor: Double = 0.0, // Indicates expense predictability (0-1)
    val isVariableAmount: Boolean = false,
    val estimatedMinAmount: Double? = null,
    val estimatedMaxAmount: Double? = null
)

// Internal model for tracking predictions (not exposed in API)
internal data class TransactionPrediction(
    val description: String,
    val category: String,
    val predictedDate: LocalDate,
    val predictedAmount: Double,
    val amountStdDev: Double,
    val dateStdDev: Double,
    val confidence: Double
)
