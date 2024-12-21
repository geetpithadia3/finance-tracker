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
    //    val sharingService: SharingService
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
              accountId = account.id!!)

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
              account = it.accountId)
        }
  }

  //  @Transactional
  //  override fun updateWithShares(transactionRequest: UpdateTransactionSharesRequest, user: User)
  // {
  //    val account =
  //        accountPersistence.findByIdAndUser(transactionRequest.account, user)
  //            ?: throw RuntimeException("Account not found for user")
  //
  //    val category =
  //        categoryPersistence.findByNameAndUser(transactionRequest.category.name, user)
  //            ?: throw RuntimeException("Category not found: ${transactionRequest.category.name}")
  //
  //    var sharedTransaction: Transaction? = null
  //    var userShare = 0.0
  //
  //    if (transactionRequest.splitShares.isNotEmpty()) {
  //      val externalAccount =
  //          accountPersistence.findByUser(user).firstOrNull { it.org ==
  // Organization.Splitwise.name }
  //              ?: throw RuntimeException("Splitwise account not found for user")
  //
  //      if (user.externalId.isNullOrEmpty() || user.externalKey.isNullOrEmpty()) {
  //        throw RuntimeException("User's external ID and key are required for split transactions")
  //      }
  //
  //      val totalOwedShare = transactionRequest.splitShares.sumOf { it.owedShare }
  //      val updatedSplitShares =
  //          transactionRequest.splitShares.map {
  //            it.copy(
  //                owedShare =
  //                    (it.owedShare / totalOwedShare * transactionRequest.amount)
  //                        .toBigDecimal()
  //                        .setScale(2, RoundingMode.HALF_UP)
  //                        .toDouble(),
  //                paidShare = 0.0)
  //          }
  //
  //      val totalOwedShareAfterCalculation = updatedSplitShares.sumOf { it.owedShare }
  //      updatedSplitShares.first { it.userId == user.externalId }.paidShare =
  //          totalOwedShareAfterCalculation
  //
  //      val expenseId =
  //          sharingService.addExpense(
  //              user.externalKey!!,
  //              totalOwedShareAfterCalculation,
  //              transactionRequest.description,
  //              transactionRequest.occurredOn,
  //              updatedSplitShares)
  //
  //      userShare = updatedSplitShares.first { it.userId == user.externalId }.owedShare
  //
  //      sharedTransaction =
  //          Transaction(
  //              type = TransactionType.DEBIT,
  //              subType = TransactionSubType.STANDARD,
  //              category = category,
  //              description = transactionRequest.description,
  //              amount = userShare,
  //              occurredOn = transactionRequest.occurredOn,
  //              externalId = expenseId,
  //              lastSyncedAt = LocalDateTime.now(),
  //              accountId = externalAccount.id!!)
  //    }
  //
  //    val transferCategory =
  //        categoryPersistence.findByNameAndUser("Transfer", user)
  //            ?: throw RuntimeException("Transfer category not found")
  //
  //    val debitTransaction =
  //        Transaction(
  //            id = transactionRequest.id,
  //            linkedTransaction = sharedTransaction,
  //            subType = TransactionSubType.SHARED,
  //            lastSyncedAt = LocalDateTime.now(),
  //            accountId = account.id!!)
  //
  //    val creditTransaction =
  //        Transaction(
  //            description = "Transfer to Splitwise",
  //            type = TransactionType.CREDIT,
  //            subType = TransactionSubType.STANDARD,
  //            category = transferCategory,
  //            amount = transactionRequest.amount - userShare,
  //            occurredOn = sharedTransaction?.occurredOn,
  //            lastSyncedAt = LocalDateTime.now(),
  //            accountId = sharedTransaction?.accountId!!)
  //
  //    transactionPersistence.update(debitTransaction)
  //    transactionPersistence.save(creditTransaction)
  //  }
  //
  //  override fun syncWithSplitwise(request: SyncAccountRequest, user: User) {
  //    if (user.externalId.isNullOrEmpty() or user.externalKey.isNullOrEmpty()) {
  //      throw RuntimeException("Please provide external id and key")
  //    }
  //
  //    accountPersistence.findByIdAndUser(request.accountId, user)
  //        ?: throw RuntimeException("Account not found for user")
  //
  //    val lastSyncDate =
  //        transactionPersistence.getLastSyncTimeForAccount(request.accountId)
  //            ?: LocalDate.now().minusMonths(3)
  //
  //    val transactions: List<SharingTransaction> =
  //        sharingService.getTransactionsForUser(
  //            user.externalId!!, user.externalKey!!, lastSyncDate.atTime(0, 0))
  //
  //    transactions.forEach { transaction ->
  //      val existingTransaction =
  //          transactionPersistence.findByExternalId(transaction.id, request.accountId)
  //
  //      val category =
  //          categoryPersistence.findByNameAndUser(transaction.category, user)
  //              ?: throw RuntimeException("Category not found: ${transaction.category}")
  //
  //      if (existingTransaction == null) {
  //        val newTransaction =
  //            Transaction(
  //                type = TransactionType.fromString(transaction.type.uppercase()),
  //                amount = transaction.amount,
  //                description = transaction.description,
  //                occurredOn = transaction.occurredOn,
  //                externalId = transaction.id,
  //                accountId = request.accountId,
  //                category = category,
  //                lastSyncedAt = LocalDateTime.now())
  //
  //        transactionPersistence.save(newTransaction)
  //      } else if (existingTransaction.lastSyncedAt!! < transaction.updatedAt) {
  //        val updatedTransaction =
  //            existingTransaction.copy(
  //                amount = transaction.amount,
  //                description = transaction.description,
  //                occurredOn = transaction.occurredOn,
  //                externalId = transaction.id,
  //                category = category,
  //                lastSyncedAt = LocalDateTime.now())
  //        transactionPersistence.save(updatedTransaction)
  //      }
  //    }
  //  }
}
