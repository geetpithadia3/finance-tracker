package com.financetracker.application.ports.input

import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.ListExpensesByMonthRequest
import com.financetracker.infrastructure.adapters.inbound.dto.response.ExpenseResponse

interface ExpenseManagementUseCase {

  fun list(request: ListExpensesByMonthRequest, user: User): List<ExpenseResponse>
}
