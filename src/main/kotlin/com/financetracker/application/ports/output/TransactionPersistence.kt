package com.financetracker.application.ports.output

import com.financetracker.domain.account.model.TransactionType
import com.financetracker.domain.model.Account
import com.financetracker.domain.model.Transaction
import java.time.LocalDate
import java.time.LocalDateTime

interface TransactionPersistence {

  fun save(transaction: Transaction): Long

  fun findByAccountInAndTypeAndOccurredOnBetween(
      accounts: List<Account>,
      type: TransactionType,
      startDate: LocalDate,
      endDate: LocalDate
  ): List<Transaction>

  fun getLastSyncTimeForAccount(account: String): LocalDateTime

  fun findByExternalId(externalId: String): Transaction?
}
