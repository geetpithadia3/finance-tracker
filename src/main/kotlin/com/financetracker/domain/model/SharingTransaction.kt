package com.financetracker.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class SharingTransaction(
    val id: String,
    val amount: Double,
    val occurredOn: LocalDate,
    val description: String,
    val category: String,
    val type: String,
    val updatedAt: LocalDateTime
)
