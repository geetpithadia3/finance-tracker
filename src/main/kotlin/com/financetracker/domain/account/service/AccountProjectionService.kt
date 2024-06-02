package com.financetracker.domain.account.service

import com.financetracker.domain.account.events.AccountEvent
import com.financetracker.domain.account.projections.AccountBalanceProjection
import com.financetracker.domain.account.projections.AccountTransactionsProjection
import org.springframework.stereotype.Service

@Service
class AccountProjectionService {
  fun getAccountBalance(events: List<AccountEvent>): AccountBalanceProjection {
    return AccountBalanceProjection.build(events = events)
  }

  fun getAccountTransactions(events: List<AccountEvent>): AccountTransactionsProjection {
    return AccountTransactionsProjection.build(events)
  }
}
