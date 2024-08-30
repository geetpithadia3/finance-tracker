package com.financetracker.application

import com.financetracker.application.ports.input.ExpenseManagementUseCase
import com.financetracker.application.ports.output.AccountPersistence
import com.financetracker.application.ports.output.TransactionPersistence
import com.financetracker.domain.model.TransactionType
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.ListExpensesByMonthRequest
import com.financetracker.infrastructure.adapters.inbound.dto.response.ExpenseResponse
import org.springframework.stereotype.Service

@Service
class ExpenseService(
    private val accountPersistence: AccountPersistence,
    private val transactionPersistence: TransactionPersistence
) : ExpenseManagementUseCase {
  override fun list(request: ListExpensesByMonthRequest, user: User): List<ExpenseResponse> {
    val startDate = request.yearMonth.atDay(1)
    val endDate = request.yearMonth.atEndOfMonth()
    val accounts = accountPersistence.findByUser(user)
    val expenses =
        transactionPersistence.findByAccountInAndTypeAndOccurredOnBetween(
            accounts, TransactionType.EXPENSE, startDate, endDate)

    return expenses.map {
      ExpenseResponse(
          id = it.id,
          type = it.type,
          category = it.category,
          description = it.description,
          amount = it.amount,
          occurredOn = it.occurredOn,
          account = it.account)
    }
  }
}
