package com.financetracker.application

import com.financetracker.application.ports.input.DashboardManagementUseCase
import com.financetracker.application.ports.output.AccountPersistence
import com.financetracker.application.ports.output.TransactionPersistence
import com.financetracker.domain.model.Category
import com.financetracker.domain.model.TransactionType
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.response.DashboardDetailsResponse
import com.financetracker.infrastructure.adapters.inbound.dto.response.ExpenseResponse
import org.springframework.stereotype.Service
import java.time.YearMonth

@Service
class DashboardService(
    private val accountPersistence: AccountPersistence,
    private val transactionPersistence: TransactionPersistence
) : DashboardManagementUseCase {
  override fun getMonthDetails(yearMonth: YearMonth, user: User): DashboardDetailsResponse {
    val startDate = yearMonth.atDay(1)
    val endDate = yearMonth.atEndOfMonth()
    val accounts = accountPersistence.findByUser(user)

    val expenses =
        transactionPersistence
            .findByAccountInAndTypeAndIsDeletedAndOccurredOnBetween(
                accounts, TransactionType.DEBIT, false, startDate, endDate)
            .filter { (it.category != Category.SAVINGS) && (it.category != Category.TRANSFER) }
            .map {
              ExpenseResponse(
                  id = it.id!!,
                  type = it.type!!,
                  category = it.category,
                  description = it.description,
                  amount = it.amount,
                  occurredOn = it.occurredOn,
                  account = it.accountId)
            }
    val income =
        transactionPersistence
            .findByAccountInAndTypeAndIsDeletedAndOccurredOnBetween(
                accounts, TransactionType.CREDIT, false, startDate, endDate)
            .filter { (it.category != Category.INCOME) }
            .map {
              ExpenseResponse(
                  id = it.id!!,
                  type = it.type!!,
                  category = it.category,
                  description = it.description,
                  amount = it.amount,
                  occurredOn = it.occurredOn,
                  account = it.accountId)
            }
    val savings =
        transactionPersistence
            .findByAccountInAndTypeAndIsDeletedAndOccurredOnBetween(
                accounts, TransactionType.DEBIT, false, startDate, endDate)
            .filter { (it.category == Category.SAVINGS) }
            .map {
              ExpenseResponse(
                  id = it.id!!,
                  type = it.type!!,
                  category = it.category,
                  description = it.description,
                  amount = it.amount,
                  occurredOn = it.occurredOn,
                  account = it.accountId)
            }

    return DashboardDetailsResponse(savings = savings, expenses = expenses, income = income)
  }
}
