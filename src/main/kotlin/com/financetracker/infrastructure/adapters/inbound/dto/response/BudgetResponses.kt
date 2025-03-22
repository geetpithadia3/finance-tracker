package com.financetracker.infrastructure.adapters.inbound.dto.response

import java.time.YearMonth
import java.util.*

data class BudgetResponse(
    val id: UUID,
    val yearMonth: YearMonth,
    val categoryLimits: List<CategoryBudgetResponse>
)

data class CategoryBudgetResponse(
    val categoryId: UUID,
    val categoryName: String,
    val limit: Double
)

data class BudgetDetailsResponse(
    val id: UUID,
    val yearMonth: YearMonth,
    val categories: List<CategoryBudgetDetailsResponse>
)

data class CategoryBudgetDetailsResponse(
    val categoryId: UUID,
    val categoryName: String,
    val limit: Double,
    val spent: Double,
)
