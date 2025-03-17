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
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.YearMonth

@Service
class DashboardService(
    private val accountPersistence: AccountPersistence,
    private val transactionPersistence: TransactionPersistence,
    private val categoryPersistence: CategoryPersistence
) : DashboardManagementUseCase {

  override fun getMonthDetails(yearMonth: YearMonth, user: User): DashboardDetailsResponse {
    val (startDate, endDate) = getStartAndEndDate(yearMonth)
    val accounts = accountPersistence.findByUser(user)

    val savingsCategory = categoryPersistence.findByNameAndUser(CategoryName.SAVINGS.value, user)
    val incomeCategory = categoryPersistence.findByNameAndUser(CategoryName.INCOME.value, user)
    val transferCategory = categoryPersistence.findByNameAndUser(CategoryName.TRANSFER.value, user)
    val creditCardPaymentCategory = categoryPersistence.findByNameAndUser(CategoryName.CREDIT_CARD_PAYMENT.value, user)

    val transactions =
        transactionPersistence.findByAccountInAndOccurredOnBetween(accounts, startDate, endDate)

    val expenses =
        filterAndMapTransactions(
            transactions,
            TransactionType.DEBIT,
            listOf(),
            listOf(savingsCategory, incomeCategory, transferCategory, creditCardPaymentCategory))
    val income =
        filterAndMapTransactions(transactions, TransactionType.CREDIT, listOf(incomeCategory))
    val savings =
        filterAndMapTransactions(transactions, TransactionType.DEBIT, listOf(savingsCategory))

    return DashboardDetailsResponse(savings = savings, expenses = expenses, income = income)
  }

  override fun getExpensesByCategory(yearMonth: YearMonth, user: User): Map<String, Double> {
    val (startDate, endDate) = getStartAndEndDate(yearMonth)
    val accounts = accountPersistence.findByUser(user)

    val savingsCategory = categoryPersistence.findByNameAndUser(CategoryName.SAVINGS.value, user)
    val transferCategory = categoryPersistence.findByNameAndUser(CategoryName.TRANSFER.value, user)
    val creditCardPaymentCategory = categoryPersistence.findByNameAndUser(CategoryName.CREDIT_CARD_PAYMENT.value, user)

    val expenses =
        transactionPersistence
            .findByAccountInAndTypeAndIsDeletedAndOccurredOnBetween(
                accounts, TransactionType.DEBIT, false, startDate, endDate)
            .filter {
              it.category?.id != savingsCategory?.id && 
              it.category?.id != transferCategory?.id &&
              it.category?.id != creditCardPaymentCategory?.id
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
      filterCategories: List<Category?> = emptyList(),
      excludeCategories: List<Category?> = emptyList()
  ): List<ExpenseResponse> {
    return transactions
        .filter {
          it.type == type &&
              !it.isDeleted &&
              (filterCategories.isEmpty() ||
                  it.category?.id in filterCategories.map { category -> category?.id }) &&
              excludeCategories.none { category -> category?.id == it.category?.id } &&
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
