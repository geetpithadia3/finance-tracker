package com.financetracker.infrastructure.adapters.inbound.dto.response

import java.time.LocalDate
import java.util.*

data class GoalResponse(
    val id: UUID,
    val name: String,
    val description: String,
    val targetDate: LocalDate,
    val amountTarget: Double,
    val amountProgress: Double,
    val progressUpdates: List<GoalProgressUpdateResponse>
)

data class GoalProgressUpdateResponse(val id: UUID, val amount: Double, val updatedOn: LocalDate)
