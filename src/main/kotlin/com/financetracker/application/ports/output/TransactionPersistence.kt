package com.financetracker.application.ports.output

import com.financetracker.domain.model.Account
import com.financetracker.domain.model.Transaction
import com.financetracker.domain.model.TransactionType
import java.time.LocalDate
import java.time.YearMonth
import java.util.*

interface TransactionPersistence {

  fun save(transaction: Transaction): UUID

  fun update(transaction: Transaction): UUID

  fun findByAccountInAndTypeAndIsDeletedAndOccurredOnBetween(
      accounts: List<Account>,
      type: TransactionType,
      isDeleted: Boolean,
      startDate: LocalDate,
      endDate: LocalDate
  ): List<Transaction>

  fun findByAccountInAndOccurredOnBetween(
      accounts: List<Account>,
      startDate: LocalDate,
      endDate: LocalDate
  ): List<Transaction>

  fun getSavingsBetween(yearMonth: YearMonth, accounts: List<UUID>): Double

  fun getIncomeBetween(yearMonth: YearMonth, accounts: List<UUID>): Double

  fun getLastSyncTimeForAccount(account: UUID): LocalDate?

  fun findByExternalId(externalId: String, accountId: UUID): Transaction?
}
