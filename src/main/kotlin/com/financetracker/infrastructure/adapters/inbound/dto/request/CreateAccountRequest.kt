package com.financetracker.infrastructure.adapters.inbound.dto.request

data class CreateAccountRequest(
    val name: String,
    val initialBalance: Double,
    val currency: String
)
