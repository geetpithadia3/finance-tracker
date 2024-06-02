package com.financetracker.application.spi

import com.financetracker.domain.account.events.AccountEvent
import java.time.LocalDate
import java.util.*

interface EventStore {
  fun save(event: AccountEvent)

  fun query(accountId: UUID): List<AccountEvent>

  fun queryBetween(accountId: UUID, startDate: LocalDate, endDate: LocalDate): List<AccountEvent>
}
