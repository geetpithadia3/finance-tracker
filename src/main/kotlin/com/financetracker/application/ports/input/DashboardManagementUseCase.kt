package com.financetracker.application.ports.input

import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.response.DashboardDetailsResponse
import java.time.YearMonth

interface DashboardManagementUseCase {

  fun getMonthDetails(yearMonth: YearMonth, user: User): DashboardDetailsResponse
  fun getExpensesByCategory(yearMonth: YearMonth, user: User): Map<String, Double>
}
