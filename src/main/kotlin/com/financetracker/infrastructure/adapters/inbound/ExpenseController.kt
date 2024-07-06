package com.financetracker.infrastructure.adapters.inbound

import com.financetracker.application.ExpensesApplicationService
import com.financetracker.application.queries.TransactionsForMonthQuery
import com.financetracker.domain.account.projections.MonthTransactionsView
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ExpenseController(val expensesApplicationService: ExpensesApplicationService) {

  @PostMapping("/transactions/list")
  fun transactions(
      @RequestBody request: TransactionsForMonthQuery
  ): ResponseEntity<List<MonthTransactionsView>> {
    return ResponseEntity.ok(expensesApplicationService.getExpenses(request))
  }
}
