package com.financetracker.domain.goal.projection

import java.time.LocalDate
import java.util.*

data class GoalView(
    val id: UUID,
    val name: String,
    val description: String,
    val amountProgress: Double,
    val amountTarget: Double,
    val targetDate: LocalDate,
    val amountPerPayPeriod: Double
)
