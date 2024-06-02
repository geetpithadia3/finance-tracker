package com.financetracker.infrastructure.adapters.inbound.controller

import com.financetracker.application.ReportApplicationService
import com.financetracker.application.api.dto.MonthlyExpenseRequest
import com.financetracker.domain.account.projections.PeriodTransactionProjection
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ReportController(private val reportApplicationService: ReportApplicationService) {
  @PostMapping("/report")
  fun monthlyReport(
      @RequestBody request: MonthlyExpenseRequest
  ): ResponseEntity<PeriodTransactionProjection> {
    return ResponseEntity.ok(reportApplicationService.queryExpenseReportFor(request))
  }
}
