package com.financetracker.domain.account.service

import com.financetracker.domain.account.events.AccountEvent
import com.financetracker.domain.account.projections.AccountPeriodTransactionProjection
import org.springframework.stereotype.Service

@Service
class ReportProjectionService {
  fun getExpenseReportForAccount(events: List<AccountEvent>): AccountPeriodTransactionProjection {
    return AccountPeriodTransactionProjection.build(events = events)
  }
}
