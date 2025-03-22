package com.financetracker.application

import com.financetracker.application.ports.input.RecurringTransactionManagementUseCase
import com.financetracker.application.ports.output.AccountPersistence
import com.financetracker.application.ports.output.CategoryPersistence
import com.financetracker.application.ports.output.RecurringTransactionPersistence
import com.financetracker.application.ports.output.TransactionPersistence
import com.financetracker.domain.model.RecurringTransaction
import com.financetracker.domain.model.Transaction
import com.financetracker.domain.model.TransactionType
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.RecurrenceRequest
import com.financetracker.infrastructure.adapters.inbound.dto.response.RecurringTransactionResponse
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.RecurrenceFrequency
import java.time.LocalDate
import java.time.YearMonth
import java.util.*
import org.springframework.stereotype.Service

@Service
class RecurringTransactionService(
    private val recurringTransactionPersistence: RecurringTransactionPersistence,
    private val transactionPersistence: TransactionPersistence,
    private val accountPersistence: AccountPersistence,
    private val categoryPersistence: CategoryPersistence
) : RecurringTransactionManagementUseCase {

  override fun createOrUpdateFromTransaction(
      transactionId: UUID,
      recurrenceRequest: RecurrenceRequest,
      user: User
  ): UUID {
    // Find the transaction to base the recurring transaction on
    val transaction =
        transactionPersistence.findById(transactionId)
            ?: throw NoSuchElementException("Transaction not found: $transactionId")

    // Check if this transaction already has a recurring transaction
    val existingRecurringTransaction =
        if (recurrenceRequest.id != null) {
          recurringTransactionPersistence.findById(recurrenceRequest.id)
        } else {
          null
        }

    // Create or update the recurring transaction
    val recurringTransaction =
        RecurringTransaction(
            id = existingRecurringTransaction?.id,
            description = transaction.description ?: "",
            amount = transaction.amount,
            categoryId =
                transaction.category?.id
                    ?: throw IllegalStateException("Transaction must have a category"),
            accountId = transaction.accountId,
            frequency = recurrenceRequest.frequency,
            startDate = recurrenceRequest.startDate,
            endDate = recurrenceRequest.endDate,
            dateFlexibility = recurrenceRequest.dateFlexibility,
            rangeStart = recurrenceRequest.rangeStart,
            rangeEnd = recurrenceRequest.rangeEnd,
            preference = recurrenceRequest.preference,
            priority = recurrenceRequest.priority,
            isActive = true,
            lastMatchedTransactionId = transactionId,
            isVariableAmount = recurrenceRequest.isVariableAmount,
            estimatedMinAmount = recurrenceRequest.estimatedMinAmount,
            estimatedMaxAmount = recurrenceRequest.estimatedMaxAmount)

    return if (existingRecurringTransaction != null) {
      recurringTransactionPersistence.update(recurringTransaction)
    } else {
      recurringTransactionPersistence.save(recurringTransaction)
    }
  }

  override fun getProjectedTransactions(yearMonth: YearMonth, user: User): List<Transaction> {
    val accounts = accountPersistence.findByUser(user)
    val recurringTransactions = recurringTransactionPersistence.findByAccountsAndActive(accounts)

    val startDate = yearMonth.atDay(1)
    val endDate = yearMonth.atEndOfMonth()

    return recurringTransactions.flatMap { recurringTx ->
      generateProjectedTransactions(recurringTx, startDate, endDate)
    }
  }

  override fun findByAccountsAndActive(user: User): List<RecurringTransactionResponse> {
    val accounts = accountPersistence.findByUser(user)
    return recurringTransactionPersistence.findByAccountsAndActive(accounts).map { it.toResponse() }
  }

  override fun findById(id: UUID, user: User): RecurringTransactionResponse? {
    val recurringTransaction = recurringTransactionPersistence.findById(id) ?: return null

    // Verify the user has access to this recurring transaction
    val accounts = accountPersistence.findByUser(user)
    if (accounts.none { it.id == recurringTransaction.accountId }) {
      return null
    }

    return recurringTransaction.toResponse()
  }

  override fun delete(id: UUID, user: User) {
    val recurringTransaction = recurringTransactionPersistence.findById(id) ?: return

    // Verify the user has access to this recurring transaction
    val accounts = accountPersistence.findByUser(user)
    if (accounts.none { it.id == recurringTransaction.accountId }) {
      throw SecurityException("User does not have access to this recurring transaction")
    }

    recurringTransactionPersistence.delete(id)
  }

  override fun updateStatus(id: UUID, isActive: Boolean, user: User): RecurringTransactionResponse {
    // Find the recurring transaction
    val recurringTransaction =
        recurringTransactionPersistence.findById(id)
            ?: throw NoSuchElementException("Recurring transaction not found: $id")

    // Verify the user has access to this recurring transaction
    val accounts = accountPersistence.findByUser(user)
    if (accounts.none { it.id == recurringTransaction.accountId }) {
      throw SecurityException("User does not have access to this recurring transaction")
    }

    // Update the status
    val updatedRecurringTransaction = recurringTransaction.copy(isActive = isActive)
    recurringTransactionPersistence.update(updatedRecurringTransaction)

    return updatedRecurringTransaction.toResponse()
  }

  private fun generateProjectedTransactions(
      recurringTransaction: RecurringTransaction,
      startDate: LocalDate,
      endDate: LocalDate
  ): List<Transaction> {
    val projectedDates = generateDateSequence(recurringTransaction, startDate, endDate)

    // Get the category
    val category =
        categoryPersistence.findById(recurringTransaction.categoryId)
            ?: throw IllegalStateException("Category not found: ${recurringTransaction.categoryId}")

    return projectedDates.map { date ->
      Transaction(
          id = null, // Projected transactions don't have IDs
          type = TransactionType.DEBIT, // Default to expense
          category = category,
          description = "${recurringTransaction.description} (Projected)",
          amount = recurringTransaction.amount,
          occurredOn = date,
          accountId = recurringTransaction.accountId,
          isDeleted = false,
          refunded = false,
          personalShare = recurringTransaction.amount,
          owedShare = 0.0)
    }
  }

  private fun generateDateSequence(
      recurringTransaction: RecurringTransaction,
      startDate: LocalDate,
      endDate: LocalDate
  ): List<LocalDate> {
    var currentDate = recurringTransaction.startDate
    val dates = mutableListOf<LocalDate>()

    while (currentDate <= endDate &&
        (recurringTransaction.endDate == null || currentDate <= recurringTransaction.endDate)) {
      if (currentDate >= startDate) {
        dates.add(currentDate)
      }
      currentDate = calculateNextDate(currentDate, recurringTransaction.frequency)
    }

    return dates
  }

  private fun calculateNextDate(currentDate: LocalDate, frequency: RecurrenceFrequency): LocalDate {
    return when (frequency) {
      RecurrenceFrequency.DAILY -> currentDate.plusDays(1)
      RecurrenceFrequency.WEEKLY -> currentDate.plusWeeks(1)
      RecurrenceFrequency.BIWEEKLY -> currentDate.plusWeeks(2)
      RecurrenceFrequency.MONTHLY -> currentDate.plusMonths(1)
      RecurrenceFrequency.YEARLY -> currentDate.plusYears(1)
      RecurrenceFrequency.FOUR_WEEKLY -> currentDate.plusWeeks(4)
    }
  }

  private fun RecurringTransaction.toResponse(): RecurringTransactionResponse {
    val category =
        categoryPersistence.findById(this.categoryId)
            ?: throw IllegalStateException("Category not found: ${this.categoryId}")

    return RecurringTransactionResponse(
        id = this.id!!,
        description = this.description,
        amount = this.amount,
        category = category,
        accountId = this.accountId,
        frequency = this.frequency,
        startDate = this.startDate,
        endDate = this.endDate,
        dateFlexibility = this.dateFlexibility,
        rangeStart = this.rangeStart,
        rangeEnd = this.rangeEnd,
        preference = this.preference,
        priority = this.priority,
        isActive = this.isActive,
        lastMatchedTransactionId = this.lastMatchedTransactionId,
        isVariableAmount = this.isVariableAmount,
        estimatedMinAmount = this.estimatedMinAmount,
        estimatedMaxAmount = this.estimatedMaxAmount)
  }
}
