package com.financetracker.application

import com.financetracker.application.ports.input.RecurringTransactionManagementUseCase
import com.financetracker.application.ports.input.TransactionManagementUseCase
import com.financetracker.application.ports.output.AccountPersistence
import com.financetracker.application.ports.output.CategoryPersistence
import com.financetracker.application.ports.output.TransactionPersistence
import com.financetracker.domain.model.Transaction
import com.financetracker.domain.model.TransactionType
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.AddTransactionRequest
import com.financetracker.infrastructure.adapters.inbound.dto.request.ListTransactionsByMonthRequest
import com.financetracker.infrastructure.adapters.inbound.dto.request.UpdateTransactionRequest
import com.financetracker.infrastructure.adapters.inbound.dto.response.RecurrenceResponse
import com.financetracker.infrastructure.adapters.inbound.dto.response.TransactionResponse
import jakarta.transaction.Transactional
import kotlin.math.abs
import org.springframework.stereotype.Service

@Service
class TransactionService(
    val transactionPersistence: TransactionPersistence,
    val accountPersistence: AccountPersistence,
    val categoryPersistence: CategoryPersistence,
    val recurringTransactionManagementUseCase: RecurringTransactionManagementUseCase
) : TransactionManagementUseCase {

  @Transactional
  override fun add(requests: List<AddTransactionRequest>, user: User) {
    requests.forEach { transactionRequest ->
      val account =
          accountPersistence.findByIdAndUser(transactionRequest.accountId, user)
              ?: throw RuntimeException("Account not found for user")

      val category =
          categoryPersistence.findByIdAndUser(transactionRequest.categoryId, user)
              ?: throw RuntimeException("Category not found or doesn't belong to user")

      val transaction =
          Transaction(
              type = TransactionType.fromString(transactionRequest.type.uppercase()),
              category = category,
              description = transactionRequest.description,
              amount = abs(transactionRequest.amount),
              occurredOn = transactionRequest.occurredOn,
              accountId = account.id!!)

      transactionPersistence.save(transaction)
    }
  }

  @Transactional
  override fun update(requests: List<UpdateTransactionRequest>, user: User) {
    requests.forEach { transactionRequest ->
      val account =
          accountPersistence.findByIdAndUser(transactionRequest.account, user)
              ?: throw RuntimeException("Account not found for user")

      val category =
          categoryPersistence.findByIdAndUser(transactionRequest.categoryId, user)
              ?: throw RuntimeException("Category not found or doesn't belong to user")

      val transaction =
          Transaction(
              id = transactionRequest.id,
              category = category,
              description = transactionRequest.description,
              occurredOn = transactionRequest.occurredOn,
              isDeleted = transactionRequest.deleted,
              amount = transactionRequest.amount,
              accountId = account.id!!,
              refunded = transactionRequest.refunded,
              personalShare = transactionRequest.personalShare,
              owedShare = transactionRequest.owedShare,
              shareMetadata = transactionRequest.shareMetadata)

      // Update the transaction first
      val updatedTransactionId = transactionPersistence.update(transaction)

      // If there's a recurrence request, handle it separately
      if (transactionRequest.recurrence != null) {
        recurringTransactionManagementUseCase.createOrUpdateFromTransaction(
            transactionId = updatedTransactionId,
            recurrenceRequest = transactionRequest.recurrence,
            user = user)
      }
    }
  }

  override fun list(
      request: ListTransactionsByMonthRequest,
      user: User
  ): List<TransactionResponse> {
    val startDate = request.yearMonth.atDay(1)
    val endDate = request.yearMonth.atEndOfMonth()
    val accounts = accountPersistence.findByUser(user)
    val transactions =
        transactionPersistence.findByAccountInAndOccurredOnBetween(accounts, startDate, endDate)

    // Get all recurring transactions for these accounts
    val recurringTransactions =
        recurringTransactionManagementUseCase.findByAccountsAndActive(user).associateBy {
          it.lastMatchedTransactionId
        }

    return transactions
        .filter { !it.isDeleted }
        .map { transaction ->
          val recurringTransaction = transaction.id?.let { recurringTransactions[it] }

          TransactionResponse(
              id = transaction.id!!,
              type = transaction.type!!.value,
              category = transaction.category!!,
              description = transaction.description!!,
              amount = transaction.amount,
              occurredOn = transaction.occurredOn!!,
              account = transaction.accountId,
              refunded = transaction.refunded,
              personalShare = transaction.personalShare,
              owedShare = transaction.owedShare,
              shareMetadata = transaction.shareMetadata,
              recurrence =
                  recurringTransaction?.let {
                    RecurrenceResponse(
                        id = it.id,
                        frequency = it.frequency,
                        startDate = it.startDate,
                        endDate = it.endDate,
                        dateFlexibility = it.dateFlexibility,
                        rangeStart = it.rangeStart,
                        rangeEnd = it.rangeEnd,
                        preference = it.preference,
                        priority = it.priority,
                        isVariableAmount = it.isVariableAmount,
                        estimatedMinAmount = it.estimatedMinAmount,
                        estimatedMaxAmount = it.estimatedMaxAmount)
                  })
        }
  }
}
