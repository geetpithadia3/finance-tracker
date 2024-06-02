package com.financetracker.application

import com.financetracker.application.api.ReportUserCase
import com.financetracker.application.api.dto.MonthlyExpenseRequest
import com.financetracker.application.spi.EventStore
import com.financetracker.domain.account.projections.AccountPeriodTransactionProjection
import com.financetracker.domain.account.projections.PeriodTransactionProjection
import com.financetracker.domain.account.service.AccountService
import com.financetracker.domain.account.service.ReportProjectionService
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ReportApplicationService(
    private val accountService: AccountService,
    private val reportProjectionService: ReportProjectionService,
    private val eventStore: EventStore
) : ReportUserCase {
  override fun queryExpenseReportFor(request: MonthlyExpenseRequest): PeriodTransactionProjection {
    val accountIdList = accountService.findAllAccountId()
    val accountExpenseList = mutableListOf<AccountPeriodTransactionProjection>()
    val startDate = LocalDate.of(request.year.value, request.month, 1)
    val endDate = startDate.plusMonths(2)

    accountIdList.forEach {
      val accountEvents = eventStore.queryBetween(it, startDate, endDate)
      accountExpenseList.add(reportProjectionService.getExpenseReportForAccount(accountEvents))
    }

    val periodTransactionProjection = PeriodTransactionProjection(startDate, endDate)

    return periodTransactionProjection.build(accountExpenseList)
  }
}
