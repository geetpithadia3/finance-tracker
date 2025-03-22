package com.financetracker.infrastructure.adapters.inbound.dto.response

data class DashboardDetailsResponse(
    val savings: List<ExpenseResponse>,
    val income: List<ExpenseResponse>,
    val expenses: List<ExpenseResponse>
)
