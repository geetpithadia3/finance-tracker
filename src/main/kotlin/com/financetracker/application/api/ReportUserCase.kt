package com.financetracker.application.api

import com.financetracker.application.api.dto.MonthlyExpenseRequest
import com.financetracker.domain.account.projections.PeriodTransactionProjection

interface ReportUserCase {
  fun queryExpenseReportFor(request: MonthlyExpenseRequest): PeriodTransactionProjection
}
