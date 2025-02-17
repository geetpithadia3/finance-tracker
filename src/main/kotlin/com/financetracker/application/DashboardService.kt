package com.financetracker.application

import com.financetracker.application.ports.input.DashboardManagementUseCase
import com.financetracker.application.ports.output.AccountPersistence
import com.financetracker.application.ports.output.CategoryPersistence
import com.financetracker.application.ports.output.TransactionPersistence
import com.financetracker.domain.model.Category
import com.financetracker.domain.model.Transaction
import com.financetracker.domain.model.TransactionType
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.response.DashboardDetailsResponse
import com.financetracker.infrastructure.adapters.inbound.dto.response.ExpenseResponse
import java.time.LocalDate
import java.time.YearMonth
import org.springframework.stereotype.Service

@Service
class DashboardService(
    private val accountPersistence: AccountPersistence,
    private val transactionPersistence: TransactionPersistence,
    private val categoryPersistence: CategoryPersistence
) : DashboardManagementUseCase {

  override fun getMonthDetails(yearMonth: YearMonth, user: User): DashboardDetailsResponse {
    val (startDate, endDate) = getStartAndEndDate(yearMonth)
    val accounts = accountPersistence.findByUser(user)

    val savingsCategory = categoryPersistence.findByNameAndUser(CategoryName.SAVINGS.name, user)
    val incomeCategory = categoryPersistence.findByNameAndUser(CategoryName.INCOME.name, user)
    val transferCategory = categoryPersistence.findByNameAndUser(CategoryName.TRANSFER.name, user)

    val transactions =
        transactionPersistence.findByAccountInAndOccurredOnBetween(accounts, startDate, endDate)

    val expenses =
        filterAndMapTransactions(
            transactions, TransactionType.DEBIT, savingsCategory, incomeCategory, transferCategory)
    val income = filterAndMapTransactions(transactions, TransactionType.CREDIT, incomeCategory)
    val savings = filterAndMapTransactions(transactions, TransactionType.DEBIT, savingsCategory)

    return DashboardDetailsResponse(savings = savings, expenses = expenses, income = income)
  }

  override fun getExpensesByCategory(yearMonth: YearMonth, user: User): Map<String, Double> {
    val (startDate, endDate) = getStartAndEndDate(yearMonth)
    val accounts = accountPersistence.findByUser(user)

    val savingsCategory = categoryPersistence.findByNameAndUser(CategoryName.SAVINGS.name, user)
    val transferCategory = categoryPersistence.findByNameAndUser(CategoryName.TRANSFER.name, user)

    val expenses =
        transactionPersistence
            .findByAccountInAndTypeAndIsDeletedAndOccurredOnBetween(
                accounts, TransactionType.DEBIT, false, startDate, endDate)
            .filter {
              it.category?.id != savingsCategory?.id && it.category?.id != transferCategory?.id
            }

    return expenses
        .groupBy { it.category!!.name }
        .mapValues { (_, transactions) -> transactions.sumOf { it.amount } }
  }

  private fun getStartAndEndDate(yearMonth: YearMonth): Pair<LocalDate, LocalDate> {
    val startDate = yearMonth.atDay(1)
    val endDate = yearMonth.atEndOfMonth()
    return Pair(startDate, endDate)
  }

  private fun filterAndMapTransactions(
      transactions: List<Transaction>,
      type: TransactionType,
      vararg excludedCategories: Category?
  ): List<ExpenseResponse> {
    return transactions
        .filter {
          it.type == type &&
              !it.isDeleted &&
              !excludedCategories.contains(it.category) &&
              it.refunded.not()
        }
        .map {
          ExpenseResponse(
              id = it.id!!,
              type = it.type!!.value,
              category = it.category!!.name,
              description = it.description!!,
              amount = it.amount,
              occurredOn = it.occurredOn!!,
              account = it.accountId,
              personalShare = it.personalShare,
          )
        }
  }
}
