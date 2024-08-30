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

@Service
class TransactionAdapter(val transactionRepository: TransactionRepository) :
    TransactionPersistence {
  override fun save(transaction: Transaction): Long {
    return transactionRepository
        .save(
            TransactionEntity().apply {
              type = transaction.type
              category = transaction.category
              description = transaction.description
              amount = transaction.amount
              occurredOn = transaction.occurredOn
              lastSyncedOn = transaction.lastSyncedAt
              account = AccountEntity().apply { id = transaction.account }
            })
        .id
  }

  override fun findByAccountInAndTypeAndOccurredOnBetween(
      accounts: List<Account>,
      type: TransactionType,
      startDate: LocalDate,
      endDate: LocalDate
  ): List<Transaction> {
    return transactionRepository
        .findByAccountInAndTypeAndOccurredOnBetween(
            accounts = accounts.map { AccountEntity().apply { id = it.id } },
            type = type,
            startDate = startDate,
            endDate = endDate)
        .map {
          Transaction(
              id = it.id,
              type = it.type,
              description = it.description,
              occurredOn = it.occurredOn,
              amount = it.amount,
              account = it.account.id,
              category = Category.valueOf(it.category.name),
              lastSyncedAt = it.lastSyncedOn)
        }
  }

  override fun getLastSyncTimeForAccount(account: String): LocalDateTime {
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
          account = it.account.id,
          category = Category.valueOf(it.category.name),
          lastSyncedAt = it.lastSyncedOn)
    }!!
  }
}
