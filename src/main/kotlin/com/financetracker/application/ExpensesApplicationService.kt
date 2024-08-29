package com.financetracker.application

import com.financetracker.application.queries.TransactionsForMonthQuery
import com.financetracker.domain.account.model.Category
import com.financetracker.domain.account.model.TransactionType
import com.financetracker.domain.account.projections.MonthTransactionsView
import com.financetracker.infrastructure.adapters.inbound.dto.UpdateTransactionRequest
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.respository.AccountRepository
import com.financetracker.infrastructure.adapters.outbound.persistence.respository.account.TransactionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class ExpensesApplicationService(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository
) {

  fun getExpenses(
      request: TransactionsForMonthQuery,
      user: UserEntity
  ): List<MonthTransactionsView> {
    val startDate = LocalDate.of(request.year.value, request.month.value, 1)
    val endDate =
        LocalDate.of(
            request.year.value, request.month.value, request.month.length(request.year.isLeap))

    return transactionRepository
        .findByAccountUserAndOccurredOnBetween(user, startDate, endDate)
        .map { transaction ->
          MonthTransactionsView(
              id = transaction.id,
              account = transaction.account?.id ?: "",
              amount = transaction.amount,
              description = transaction.description,
              category = transaction.category,
              type = transaction.type,
              date = transaction.occurredOn)
        }
  }

  @Transactional
  fun updateExpenses(request: List<UpdateTransactionRequest>, user: UserEntity) {
    request.forEach { updateRequest ->
      val transaction =
          transactionRepository
              .findById(updateRequest.id)
              .filter { it.account?.user == user }
              .orElse(null)

      transaction?.let {
        val account = accountRepository.findByIdAndUser(updateRequest.account, user)
        account?.let {
          transaction.apply {
            category = Category.valueOf(updateRequest.category.uppercase())
            description = updateRequest.description
            amount = updateRequest.amount
            occurredOn = updateRequest.occurredOn
            deleted = updateRequest.deleted
            this.account = account
          }
          transactionRepository.save(transaction)
        }
      }
    }
  }

  fun getExpensesByCategory(
      startDate: LocalDate,
      endDate: LocalDate,
      user: UserEntity
  ): Map<Category, Double> {
    return transactionRepository
        .findByAccountUserAndOccurredOnBetween(user, startDate, endDate)
        .filter { it.type == TransactionType.DEBIT }
        .groupBy { it.category }
        .mapValues { (_, transactions) -> transactions.sumOf { it.amount } }
  }

  fun getTotalExpenses(startDate: LocalDate, endDate: LocalDate, user: UserEntity): Double {
    return transactionRepository
        .findByAccountUserAndOccurredOnBetween(user, startDate, endDate)
        .filter { it.type == TransactionType.DEBIT }
        .sumOf { it.amount }
  }
}
