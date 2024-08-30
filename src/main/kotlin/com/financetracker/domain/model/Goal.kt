package com.financetracker.domain.model

import java.time.LocalDate
import java.util.*

data class Goal(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val description: String,
    val targetDate: LocalDate,
    val amountTarget: Double,
    var amountProgress: Double = 0.0,
    val userId: UUID,
    val progressUpdates: MutableList<GoalProgressUpdate> = mutableListOf(),
    val toSavePerPayPeriod: Double
)

data class GoalProgressUpdate(
    val id: UUID = UUID.randomUUID(),
    val amount: Double,
    val updatedOn: LocalDate
)
