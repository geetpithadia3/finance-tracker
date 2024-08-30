package com.financetracker.infrastructure.adapters.inbound.dto.request

import java.time.LocalDate
import java.util.*

data class UpdateGoalProgressRequest(
    val goalId: UUID,
    val amount: Double,
    val updatedOn: LocalDate
)
