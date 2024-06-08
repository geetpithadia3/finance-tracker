package com.financetracker.application

import com.financetracker.application.commands.AddTransactionCommand
import com.financetracker.application.commands.FetchMonthSplitwiseTransactionsCommand
import com.financetracker.domain.account.model.Category
import com.financetracker.domain.account.model.TransactionType
import com.financetracker.domain.account.valueObjects.Currency
import com.financetracker.domain.account.valueObjects.Money
import com.financetracker.domain.account.valueObjects.TransactionDetails
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.SplitwiseTransaction
import com.financetracker.infrastructure.adapters.outbound.persistence.respository.SplitwiseTransactionRepository
import com.financetracker.infrastructure.adapters.outbound.splitwise.SplitwiseService
import com.financetracker.infrastructure.adapters.outbound.splitwise.dto.SplitwiseExpense
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.Month
import java.time.Year

@Service
class SplitwiseApplicationService(
    val commandGateway: CommandGateway,
    val splitwiseService: SplitwiseService,
    val splitwiseTransactionRepository: SplitwiseTransactionRepository
) {

  fun syncMonthExpenses(request: FetchMonthSplitwiseTransactionsCommand) {
    val dateBoundaries = dateBoundariesForMonth(request.month, request.year)
    val userId: Long = 15110641
    val expenseList =
        splitwiseService.getExpensesBetween(dateBoundaries.first, dateBoundaries.second)

    expenseList.expenses
        .filter { isExpenseNotSynced(it) }
        .forEach { expense ->
          expense.users
              .filter { it.userId == userId }
              .map { ue ->
                syncExpense(expense)
                commandGateway.send<AddTransactionCommand>(
                    AddTransactionCommand(
                        accountId = request.account,
                        type = TransactionType.DEBIT,
                        amount = Money(ue.owedShare.toDouble(), Currency.CAD),
                        details =
                            TransactionDetails(
                                description = expense.description,
                                category = mapCategory(expense.category.name),
                                occurredOn = expense.date.toLocalDate())))
              }
        }
  }

  private fun isExpenseNotSynced(expense: SplitwiseExpense): Boolean {
    val syncedTransaction = splitwiseTransactionRepository.findById(expense.id)
    return if (syncedTransaction.isPresent) {
      syncedTransaction.get().updatedOn < expense.date.toLocalDate()
    } else {
      true
    }
  }

  private fun syncExpense(expense: SplitwiseExpense) {
    val syncedTransaction = splitwiseTransactionRepository.findById(expense.id)
    if (syncedTransaction.isPresent) {
      syncedTransaction.get().updatedOn = expense.date.toLocalDate()
      splitwiseTransactionRepository.save(syncedTransaction.get())
    } else {
      splitwiseTransactionRepository.save(
          SplitwiseTransaction().apply {
            id = expense.id
            updatedOn = expense.date.toLocalDate()
          })
    }
  }

  fun mapCategory(category: String): Category {
    return when {
      category.uppercase() == "GENERAL" -> Category.GENERAL
      category.uppercase() == "DINING OUT" -> Category.RESTAURANT
      category.uppercase() == "LIQUOR" -> Category.DRINKS
      else -> Category.GENERAL
    }
  }

  fun dateBoundariesForMonth(month: Month, year: Year): Pair<LocalDate, LocalDate> {
    val firstDay = LocalDate.of(year.value, month.value, 1)
    val lastDay = LocalDate.of(year.value, month.value, month.maxLength())
    return Pair(firstDay, lastDay)
  }
}
