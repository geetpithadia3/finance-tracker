package com.financetracker.application.ports.input

import com.financetracker.domain.model.IncomeSource
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.AddIncomeSourceRequest
import com.financetracker.infrastructure.adapters.inbound.dto.request.UpdateIncomeSourceRequest
import java.util.*

interface IncomeSourceManagementUseCase {
  fun addIncomeSource(request: AddIncomeSourceRequest, user: User): IncomeSource

  fun updateIncomeSource(id: UUID, request: UpdateIncomeSourceRequest, user: User): IncomeSource

  fun listIncomeSources(user: User): List<IncomeSource>

  fun removeIncomeSource(id: UUID, user: User)

  fun calculateMonthlyIncome(user: User): Double
}
