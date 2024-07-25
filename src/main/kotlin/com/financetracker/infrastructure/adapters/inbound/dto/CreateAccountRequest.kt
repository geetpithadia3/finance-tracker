package com.financetracker.infrastructure.adapters.inbound.dto

data class CreateAccountRequest(
    val type: String,
    val org: String,
    val initialBalance: Double,
    val currency: String
)
