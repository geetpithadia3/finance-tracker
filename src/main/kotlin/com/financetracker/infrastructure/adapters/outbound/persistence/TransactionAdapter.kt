package com.financetracker.infrastructure.adapters.outbound.persistence

import com.financetracker.application.ports.output.TransactionPersistence
import com.financetracker.domain.model.Account
import com.financetracker.domain.model.Transaction
import com.financetracker.domain.model.TransactionType
import com.financetracker.domain.model.toEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.AccountEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.TransactionEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.toModel
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.CategoryRepository
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.TransactionRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.YearMonth
import java.util.*

@Service
class TransactionAdapter(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : TransactionPersistence {
  override fun save(transaction: Transaction): UUID {
    val categoryEntity =
        transaction.category?.let { category ->
          categoryRepository.findById(category.id!!).orElseThrow {
            NoSuchElementException("Category not found: ${category.id}")
          }
        }

    return transactionRepository
        .save(
            TransactionEntity().apply {
              type = transaction.type!!
              category = categoryEntity!!
              description = transaction.description!!
              amount = transaction.amount
              externalId = transaction.externalId
              occurredOn = transaction.occurredOn!!
              lastSyncedOn = transaction.lastSyncedAt!!
              subType = transaction.subType!!
              account = AccountEntity().apply { id = transaction.accountId }
            })
        .id
  }

  override fun update(transaction: Transaction): UUID {
    val existingEntity =
        transactionRepository.findById(transaction.id!!).orElseThrow {
          NoSuchElementException("Transaction not found: ${transaction.id}")
        }

    val categoryEntity =
        transaction.category?.let { category ->
          categoryRepository.findById(category.id!!).orElseThrow {
            NoSuchElementException("Category not found: ${category.id}")
          }
        }

    existingEntity.apply {
      category = categoryEntity ?: category
      subType = transaction.subType ?: subType
      linkedTransaction = transaction.linkedTransaction?.toEntity()
      description = transaction.description ?: description
      occurredOn = transaction.occurredOn ?: occurredOn
      lastSyncedOn = transaction.lastSyncedAt ?: lastSyncedOn
      isDeleted = transaction.isDeleted
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
              category = it.category?.toModel(),
              subType = it.subType,
              externalId = it.externalId,
              linkedTransaction =
                  it.linkedTransaction?.let {
                    Transaction(
                        id = it.id,
                        type = it.type,
                        category = it.category?.toModel(),
                        description = it.description,
                        occurredOn = it.occurredOn,
                        amount = it.amount,
                        lastSyncedAt = it.lastSyncedOn,
                        accountId = it.account.id)
                  },
              lastSyncedAt = it.lastSyncedOn)
        }
  }

  override fun findByAccountInAndOccurredOnBetween(
      accounts: List<Account>,
      startDate: LocalDate,
      endDate: LocalDate
  ): List<Transaction> {
    return transactionRepository
        .findByAccountInAndOccurredOnBetween(
            accounts = accounts.map { AccountEntity().apply { id = it.id!! } },
            startDate = startDate,
            endDate = endDate)
        .map {
          Transaction(
              id = it.id,
              type = it.type,
              description = it.description,
              occurredOn = it.occurredOn,
              amount = it.amount,
              accountId = it.account.id,
              isDeleted = it.isDeleted,
              category = it.category?.toModel(),
              subType = it.subType,
              externalId = it.externalId,
              linkedTransaction =
                  it.linkedTransaction?.let {
                    Transaction(
                        id = it.id,
                        type = it.type,
                        category = it.category?.toModel(),
                        description = it.description,
                        occurredOn = it.occurredOn,
                        amount = it.amount,
                        lastSyncedAt = it.lastSyncedOn,
                        accountId = it.account.id)
                  },
              lastSyncedAt = it.lastSyncedOn)
        }
  }

  override fun getSavingsBetween(yearMonth: YearMonth, accounts: List<UUID>): Double {
    return transactionRepository.getSavingsTotalForMonth(
        yearMonth.year, yearMonth.month.value, accounts)
  }

  override fun getIncomeBetween(yearMonth: YearMonth, accounts: List<UUID>): Double {
    return transactionRepository.getSIncomeTotalForMonth(
        yearMonth.year, yearMonth.month.value, accounts)
  }

  override fun getLastSyncTimeForAccount(account: UUID): LocalDate? {
    return transactionRepository.findLastSyncDateForAccount(account)
  }

  override fun findByExternalId(externalId: String, accountId: UUID): Transaction? {
    return transactionRepository.findByExternalIdAndAccountId(externalId, accountId)?.let {
      Transaction(
          id = it.id,
          type = it.type,
          description = it.description,
          occurredOn = it.occurredOn,
          amount = it.amount,
          accountId = it.account.id,
          category = it.category?.toModel(),
          lastSyncedAt = it.lastSyncedOn)
    }
  }
}
