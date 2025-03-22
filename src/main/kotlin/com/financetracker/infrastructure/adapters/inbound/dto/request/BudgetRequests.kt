package com.financetracker.infrastructure.adapters.inbound.dto.request

import java.time.YearMonth
import java.util.*

data class CreateBudgetRequest(
    val yearMonth: YearMonth,
    val categoryLimits: List<CategoryBudgetRequest>
)

data class CategoryBudgetRequest(val categoryId: UUID, val budgetAmount: Double)

data class UpdateBudgetRequest(
    val categoryLimits: List<CategoryBudgetRequest>,
    val yearMonth: YearMonth
)
