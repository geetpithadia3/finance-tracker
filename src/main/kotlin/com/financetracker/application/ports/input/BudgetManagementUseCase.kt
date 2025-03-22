package com.financetracker.application.ports.input

import com.financetracker.application.ReportFormat
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.CreateBudgetRequest
import com.financetracker.infrastructure.adapters.inbound.dto.response.BudgetDetailsResponse
import com.financetracker.infrastructure.adapters.inbound.dto.response.BudgetResponse
import com.financetracker.infrastructure.adapters.inbound.dto.response.CategoryResponse
import java.time.YearMonth

interface BudgetManagementUseCase {
  fun createBudget(request: CreateBudgetRequest, user: User): BudgetResponse

  fun getBudgetDetails(yearMonth: YearMonth, user: User): BudgetDetailsResponse

  fun getBudgetableCategories(user: User): List<CategoryResponse>

  fun generateMonthlyReport(yearMonth: YearMonth, user: User, format: ReportFormat): ByteArray
}
