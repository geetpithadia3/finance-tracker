package com.financetracker.infrastructure.adapters.inbound.dto

import java.time.LocalDate
import java.util.*

data class UpdateGoalRequest(val goalId: UUID, val amount: Double, val updatedOn: LocalDate)
