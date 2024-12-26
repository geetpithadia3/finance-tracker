package com.financetracker.application.ports.input

import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.CreateBudgetRequest
import com.financetracker.infrastructure.adapters.inbound.dto.request.UpdateBudgetRequest
import com.financetracker.infrastructure.adapters.inbound.dto.response.BudgetDetailsResponse
import com.financetracker.infrastructure.adapters.inbound.dto.response.BudgetResponse
import com.financetracker.infrastructure.adapters.inbound.dto.response.CategoryResponse
import java.time.YearMonth
import java.util.*

interface BudgetManagementUseCase {
  fun createBudget(request: CreateBudgetRequest, user: User): BudgetResponse

  fun getBudgetDetails(yearMonth: YearMonth, user: User): BudgetDetailsResponse

  fun updateBudget(id: UUID, request: UpdateBudgetRequest, user: User): BudgetResponse

  fun getBudgetableCategories(user: User): List<CategoryResponse>
}
