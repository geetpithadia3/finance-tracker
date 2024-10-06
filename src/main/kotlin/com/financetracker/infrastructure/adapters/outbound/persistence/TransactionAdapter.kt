package com.financetracker.infrastructure.adapters.outbound.persistence

import com.financetracker.application.ports.output.TransactionPersistence
import com.financetracker.domain.model.Account
import com.financetracker.domain.model.Category
import com.financetracker.domain.model.Transaction
import com.financetracker.domain.model.TransactionType
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.AccountEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.TransactionEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.TransactionRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.util.*

@Service
class TransactionAdapter(val transactionRepository: TransactionRepository) :
    TransactionPersistence {
  override fun save(transaction: Transaction): UUID {
    return transactionRepository
        .save(
            TransactionEntity().apply {
              type = transaction.type!!
              category = transaction.category
              description = transaction.description
              amount = transaction.amount
              occurredOn = transaction.occurredOn
              lastSyncedOn = transaction.lastSyncedAt
              account = AccountEntity().apply { id = transaction.accountId }
            })
        .id
  }

  override fun update(transaction: Transaction): UUID {
    val existingEntity =
        transactionRepository.findById(transaction.id!!).orElseThrow {
          NoSuchElementException("Transaction not found with id: ${transaction.id}")
        }

    // Update only the fields that are provided in the input transaction
    existingEntity.apply {
      category = transaction.category ?: category
      description = transaction.description ?: description
      occurredOn = transaction.occurredOn ?: occurredOn
      lastSyncedOn = transaction.lastSyncedAt ?: lastSyncedOn
      isDeleted = transaction.isDeleted
      // Only update the account if a new accountId is provided
      transaction.accountId?.let { newAccountId ->
        account = AccountEntity().apply { id = newAccountId }
      }
    }

    return transactionRepository.save(existingEntity).id
  }

  override fun findByAccountInAndTypeAndIsDeletedAndOccurredOnBetween(
      accounts: List<Account>,
      type: TransactionType,
      isDeleted: Boolean,
      startDate: LocalDate,
      endDate: LocalDate
  ): List<Transaction> {
    return transactionRepository
        .findByAccountInAndTypeAndOccurredOnBetween(
            accounts = accounts.map { AccountEntity().apply { id = it.id!! } },
            type = type,
            startDate = startDate,
            endDate = endDate)
        .filter { it.isDeleted == isDeleted }
        .map {
          Transaction(
              id = it.id,
              type = it.type,
              description = it.description,
              occurredOn = it.occurredOn,
              amount = it.amount,
              accountId = it.account.id,
              category = Category.valueOf(it.category.name),
              lastSyncedAt = it.lastSyncedOn)
        }
  }

  override fun getSavingsBetween(yearMonth: YearMonth, accounts: List<UUID>): Double {
    return transactionRepository.getTotalAmountByCategoryAndMonth(
        Category.SAVINGS.name, yearMonth.month.value, yearMonth.year, accounts)
  }

  override fun getIncomeBetween(yearMonth: YearMonth, accounts: List<UUID>): Double {
    return transactionRepository.getTotalAmountByTypeForMonth(
        TransactionType.CREDIT.name, yearMonth.month.value, yearMonth.year, accounts)
  }

  override fun getLastSyncTimeForAccount(account: UUID): LocalDateTime {
    TODO("Not yet implemented")
  }

  override fun findByExternalId(externalId: String): Transaction? {
    return transactionRepository.findByExternalId(externalId)?.let {
      Transaction(
          id = it.id,
          type = it.type,
          description = it.description,
          occurredOn = it.occurredOn,
          amount = it.amount,
          accountId = it.account.id,
          category = Category.valueOf(it.category.name),
          lastSyncedAt = it.lastSyncedOn)
    }!!
  }
}
