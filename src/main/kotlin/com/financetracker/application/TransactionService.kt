package com.financetracker.application

import com.financetracker.application.ports.input.TransactionManagementUseCase
import com.financetracker.application.ports.output.AccountPersistence
import com.financetracker.application.ports.output.CategoryPersistence
import com.financetracker.application.ports.output.TransactionPersistence
import com.financetracker.domain.model.Transaction
import com.financetracker.domain.model.TransactionSubType
import com.financetracker.domain.model.TransactionType
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.AddTransactionRequest
import com.financetracker.infrastructure.adapters.inbound.dto.request.ListTransactionsByMonthRequest
import com.financetracker.infrastructure.adapters.inbound.dto.request.UpdateTransactionRequest
import com.financetracker.infrastructure.adapters.inbound.dto.response.TransactionResponse
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import kotlin.math.abs

@Service
class TransactionService(
    val transactionPersistence: TransactionPersistence,
    val accountPersistence: AccountPersistence,
    val categoryPersistence: CategoryPersistence,
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
              subType = TransactionSubType.STANDARD,
              category = category,
              description = transactionRequest.description,
              amount = abs(transactionRequest.amount),
              occurredOn = transactionRequest.occurredOn,
              lastSyncedAt = LocalDateTime.now(),
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
              lastSyncedAt = LocalDateTime.now(),
              isDeleted = transactionRequest.deleted,
              amount = transactionRequest.amount,
              accountId = account.id!!,
              refunded = transactionRequest.refunded,
              personalShare = transactionRequest.personalShare,
              owedShare = transactionRequest.owedShare,
              shareMetadata = transactionRequest.shareMetadata)

      transactionPersistence.update(transaction)
    }
  }

  override fun list(
      request: ListTransactionsByMonthRequest,
      user: User
  ): List<TransactionResponse> {
    val startDate = request.yearMonth.atDay(1)
    val endDate = request.yearMonth.atEndOfMonth()
    val accounts = accountPersistence.findByUser(user)
    val expenses =
        transactionPersistence.findByAccountInAndOccurredOnBetween(accounts, startDate, endDate)

    return expenses
        .filter { !it.isDeleted }
        .map {
          TransactionResponse(
              id = it.id!!,
              type = it.type!!.value,
              category = it.category!!,
              description = it.description!!,
              amount = it.amount,
              shareable = it.externalId == null,
              occurredOn = it.occurredOn!!,
              account = it.accountId,
              refunded = it.refunded,
              personalShare = it.personalShare,
              owedShare = it.owedShare,
              shareMetadata = it.shareMetadata)
        }
  }
}
