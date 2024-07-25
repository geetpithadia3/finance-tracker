package com.financetracker.infrastructure.adapters.inbound.dto

data class DeleteExpenseRequest(val amount: Double, val category: String, val type: String)
