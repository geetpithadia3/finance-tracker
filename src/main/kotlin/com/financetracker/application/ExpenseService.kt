package com.financetracker.application

import com.financetracker.application.ports.input.ExpenseManagementUseCase
import com.financetracker.application.ports.output.AccountPersistence
import com.financetracker.application.ports.output.CategoryPersistence
import com.financetracker.application.ports.output.TransactionPersistence
import com.financetracker.domain.model.TransactionSubType
import com.financetracker.domain.model.TransactionType
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.ListExpensesByMonthRequest
import com.financetracker.infrastructure.adapters.inbound.dto.response.ExpenseResponse
import org.springframework.stereotype.Service

@Service
class ExpenseService(
    private val accountPersistence: AccountPersistence,
    private val transactionPersistence: TransactionPersistence,
    private val categoryPersistence: CategoryPersistence
) : ExpenseManagementUseCase {
  override fun list(request: ListExpensesByMonthRequest, user: User): List<ExpenseResponse> {
    val startDate = request.yearMonth.atDay(1)
    val endDate = request.yearMonth.atEndOfMonth()
    val accounts = accountPersistence.findByUser(user)
    
    val savingsCategory = categoryPersistence.findByNameAndUser("Savings", user)
    val transferCategory = categoryPersistence.findByNameAndUser("Transfer", user)
    
    val expenses =
        transactionPersistence.findByAccountInAndTypeAndIsDeletedAndOccurredOnBetween(
            accounts, TransactionType.DEBIT, false, startDate, endDate)

    return expenses
        .filter {
          (it.category?.id != savingsCategory?.id) &&
              (it.category?.id != transferCategory?.id) &&
              (it.subType!! != TransactionSubType.SHARED)
        }
        .map {
          ExpenseResponse(
              id = it.id!!,
              type = it.type!!.value,
              category = it.category!!.name,
              description = it.description!!,
              amount = it.amount,
              shareable = it.externalId == null,
              occurredOn = it.occurredOn!!,
              account = it.accountId)
        }
  }
}
