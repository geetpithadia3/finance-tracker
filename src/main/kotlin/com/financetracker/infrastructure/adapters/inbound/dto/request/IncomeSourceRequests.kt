package com.financetracker.infrastructure.adapters.inbound.dto.request

import java.time.LocalDateTime

data class AddIncomeSourceRequest(
    val payFrequency: String,
    val payAmount: Double,
    val nextPayDate: LocalDateTime
)

data class UpdateIncomeSourceRequest(
    val payFrequency: String,
    val payAmount: Double,
    val nextPayDate: LocalDateTime
)
