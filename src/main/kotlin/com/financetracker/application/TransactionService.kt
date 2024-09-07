package com.financetracker.application

import com.financetracker.application.ports.input.TransactionManagementUseCase
import com.financetracker.application.ports.output.AccountPersistence
import com.financetracker.application.ports.output.SharingService
import com.financetracker.application.ports.output.TransactionPersistence
import com.financetracker.domain.model.*
import com.financetracker.infrastructure.adapters.inbound.dto.request.AddTransactionRequest
import com.financetracker.infrastructure.adapters.inbound.dto.request.SyncAccountRequest
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TransactionService(
    val transactionPersistence: TransactionPersistence,
    val accountPersistence: AccountPersistence,
    val sharingService: SharingService
) : TransactionManagementUseCase {

  @Transactional
  override fun add(requests: List<AddTransactionRequest>, user: User) {
    requests.forEach { transactionRequest ->
      val account = accountPersistence.findByIdAndUser(transactionRequest.accountId, user)
          ?: throw RuntimeException("Account not found for user")
  
      val transaction = Transaction(
          type = TransactionType.valueOf(transactionRequest.type.uppercase()),
          category = Category.valueOf(transactionRequest.category.uppercase()),
          description = transactionRequest.description,
          amount = transactionRequest.amount,
          occurredOn = transactionRequest.occurredOn,
          lastSyncedAt = LocalDateTime.now(),
          accountId = account.id!!)
  
      transactionPersistence.save(transaction)
  
      // Update account balance
      when (transaction.type) {
        TransactionType.INCOME -> account.balance += transaction.amount
        TransactionType.EXPENSE -> account.balance -= transaction.amount
        TransactionType.TRANSFER_DEBIT -> account.balance -= transaction.amount
        TransactionType.TRANSFER_CREDIT -> account.balance += transaction.amount
      }
  
      accountPersistence.save(account)
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
