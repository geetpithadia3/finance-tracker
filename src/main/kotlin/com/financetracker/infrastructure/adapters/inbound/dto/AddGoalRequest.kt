package com.financetracker.infrastructure.adapters.inbound.dto

import java.time.LocalDate

data class AddGoalRequest(
    val name: String,
    val description: String,
    val targetDate: LocalDate,
    val amountTarget: Double = 0.0,
    val amountProgress: Double = 0.0
)
