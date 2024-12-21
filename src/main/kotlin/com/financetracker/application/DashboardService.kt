package com.financetracker.application

import com.financetracker.application.ports.input.DashboardManagementUseCase
import com.financetracker.application.ports.output.AccountPersistence
import com.financetracker.application.ports.output.CategoryPersistence
import com.financetracker.application.ports.output.TransactionPersistence
import com.financetracker.domain.model.TransactionType
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.response.DashboardDetailsResponse
import com.financetracker.infrastructure.adapters.inbound.dto.response.ExpenseResponse
import org.springframework.stereotype.Service
import java.time.YearMonth

@Service
class DashboardService(
    private val accountPersistence: AccountPersistence,
    private val transactionPersistence: TransactionPersistence,
    private val categoryPersistence: CategoryPersistence
) : DashboardManagementUseCase {
  override fun getMonthDetails(yearMonth: YearMonth, user: User): DashboardDetailsResponse {
    val startDate = yearMonth.atDay(1)
    val endDate = yearMonth.atEndOfMonth()
    val accounts = accountPersistence.findByUser(user)

    val savingsCategory = categoryPersistence.findByNameAndUser("Savings", user)
    val incomeCategory = categoryPersistence.findByNameAndUser("Income", user)
    val transferCategory = categoryPersistence.findByNameAndUser("Transfer", user)

    val transactions =
        transactionPersistence.findByAccountInAndOccurredOnBetween(accounts, startDate, endDate)

    val expenses =
        transactions
            .filter {
              (it.type == TransactionType.DEBIT && it.category?.id != savingsCategory?.id) &&
                  (it.category?.id != incomeCategory?.id) &&
                  (it.category?.id != transferCategory?.id) &&
                  !it.isDeleted
            }
            .map {
              ExpenseResponse(
                  id = it.id!!,
                  type = it.type!!.value,
                  category = it.category!!.name,
                  description = it.description!!,
                  amount = it.amount,
                  occurredOn = it.occurredOn!!,
                  shareable = it.externalId == null,
                  account = it.accountId)
            }
    val income =
        transactions
            .filter { (it.category?.id == incomeCategory?.id) && !it.isDeleted }
            .map {
              ExpenseResponse(
                  id = it.id!!,
                  type = it.type!!.value,
                  category = it.category!!.name,
                  description = it.description!!,
                  amount = it.amount,
                  occurredOn = it.occurredOn!!,
                  shareable = it.externalId == null,
                  account = it.accountId)
            }

    val savings =
        transactions
            .filter { (it.category?.id == savingsCategory?.id) && !it.isDeleted }
            .map {
              ExpenseResponse(
                  id = it.id!!,
                  type = it.type!!.value,
                  category = it.category!!.name,
                  description = it.description!!,
                  amount = it.amount,
                  occurredOn = it.occurredOn!!,
                  shareable = it.externalId == null,
                  account = it.accountId)
            }

    return DashboardDetailsResponse(savings = savings, expenses = expenses, income = income)
  }

  override fun getExpensesByCategory(yearMonth: YearMonth, user: User): Map<String, Double> {
    val startDate = yearMonth.atDay(1)
    val endDate = yearMonth.atEndOfMonth()
    val accounts = accountPersistence.findByUser(user)

    val savingsCategory = categoryPersistence.findByNameAndUser("Savings", user)
    val transferCategory = categoryPersistence.findByNameAndUser("Transfer", user)

    val expenses =
        transactionPersistence
            .findByAccountInAndTypeAndIsDeletedAndOccurredOnBetween(
                accounts, TransactionType.DEBIT, false, startDate, endDate)
            .filter { transaction ->
              transaction.category?.id != savingsCategory?.id &&
                  transaction.category?.id != transferCategory?.id
            }

    return expenses
        .groupBy { it.category!!.name }
        .mapValues { (_, transactions) -> transactions.sumOf { it.amount } }
  }
}
