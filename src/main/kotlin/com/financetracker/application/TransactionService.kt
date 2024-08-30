package com.financetracker.application

import com.financetracker.application.ports.input.TransactionManagementUseCase
import com.financetracker.application.ports.output.AccountPersistence
import com.financetracker.application.ports.output.SharingService
import com.financetracker.application.ports.output.TransactionPersistence
import com.financetracker.domain.model.*
import com.financetracker.infrastructure.adapters.inbound.dto.request.AddTransactionRequest
import com.financetracker.infrastructure.adapters.inbound.dto.request.SyncAccountRequest
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TransactionService(
    val transactionPersistence: TransactionPersistence,
    val accountPersistence: AccountPersistence,
    val sharingService: SharingService
) : TransactionManagementUseCase {

  override fun add(requests: List<AddTransactionRequest>, user: User) {
    requests.map {
      val account =
          accountPersistence.findByIdAndUser(it.accountId, user)
              ?: throw RuntimeException("Account not found for user")

      account.let { acc ->
        val transaction =
            Transaction(
                type = TransactionType.valueOf(it.type.uppercase()),
                category = Category.valueOf(it.category.uppercase()),
                description = it.description,
                amount = it.amount,
                occurredOn = it.occurredOn,
                lastSyncedAt = LocalDateTime.now(),
                accountId = acc.id!!)
        transactionPersistence.save(transaction)
        it
      }
    }
  }

  override fun syncWithSplitwise(request: SyncAccountRequest, user: User) {
    if (user.externalId.isNullOrEmpty() or user.externalKey.isNullOrEmpty()) {
      throw RuntimeException("Please provide external id and key")
    }

    accountPersistence.findByIdAndUser(request.accountId, user)
        ?: throw RuntimeException("Account not found for user")

    val lastSyncTime = transactionPersistence.getLastSyncTimeForAccount(request.accountId)
    val transactions: List<SharingTransaction> =
        sharingService.getTransactionsForUser(user.externalId!!, user.externalKey!!, lastSyncTime)

    transactions.forEach { transaction ->
      val existingTransaction = transactionPersistence.findByExternalId(transaction.id)

      if (existingTransaction == null) {
        val newTransaction =
            Transaction(
                type = TransactionType.valueOf(transaction.type.uppercase()),
                amount = transaction.amount,
                description = transaction.description,
                occurredOn = transaction.occurredOn,
                externalId = transaction.id,
                accountId = request.accountId,
                category = Category.valueOf(transaction.category.uppercase()),
                lastSyncedAt = LocalDateTime.now())

        transactionPersistence.save(newTransaction)
      } else if (existingTransaction.lastSyncedAt < transaction.updatedAt) {
        val updatedTransaction =
            existingTransaction.copy(
                amount = transaction.amount,
                description = transaction.description,
                occurredOn = transaction.occurredOn,
                lastSyncedAt = LocalDateTime.now())
        transactionPersistence.save(updatedTransaction)
      }
    }
  }
}
