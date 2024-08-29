package com.financetracker.application.ports.input

import com.financetracker.application.dto.request.ListExpensesByMonthRequest
import com.financetracker.application.dto.response.ExpenseResponse
import com.financetracker.domain.model.User

interface ExpenseManagementUseCase {

  fun list(request: ListExpensesByMonthRequest, user: User): List<ExpenseResponse>
}
