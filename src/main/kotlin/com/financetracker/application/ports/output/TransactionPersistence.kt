package com.financetracker.application.ports.output

import com.financetracker.domain.model.Account
import com.financetracker.domain.model.Transaction
import com.financetracker.domain.model.TransactionType
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

interface TransactionPersistence {

  fun save(transaction: Transaction): UUID

  fun findByAccountInAndTypeAndOccurredOnBetween(
      accounts: List<Account>,
      type: TransactionType,
      startDate: LocalDate,
      endDate: LocalDate
  ): List<Transaction>

  fun getLastSyncTimeForAccount(account: UUID): LocalDateTime

  fun findByExternalId(externalId: String): Transaction?
}
