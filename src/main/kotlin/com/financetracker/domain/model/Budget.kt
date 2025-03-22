package com.financetracker.domain.model

import java.time.YearMonth
import java.util.*

data class Budget(
    val id: UUID? = null,
    val userId: UUID,
    val yearMonth: YearMonth,
    val categoryLimits: List<CategoryBudget>,
    val isActive: Boolean = true
)

data class CategoryBudget(val id: UUID? = null, val categoryId: UUID, val budgetAmount: Double)
