package com.financetracker.application.api.dto

data class AccountDTO(
    val id: String,
    val name: String,
    val type: String,
    val description: String,
    val organization: String,
    val balance: Double = 0.0
)