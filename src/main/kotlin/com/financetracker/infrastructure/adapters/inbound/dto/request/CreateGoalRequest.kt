package com.financetracker.infrastructure.adapters.inbound.dto.request

import java.time.LocalDate

data class CreateGoalRequest(
    val name: String,
    val description: String,
    val targetDate: LocalDate,
    val amountTarget: Double,
    val amountProgress: Double
)
