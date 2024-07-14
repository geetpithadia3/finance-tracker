package com.financetracker.infrastructure.adapters.inbound

import com.financetracker.application.SplitwiseApplicationService
import com.financetracker.application.commands.FetchMonthSplitwiseTransactionsCommand
import com.financetracker.application.commands.SyncSplitwiseTransactions
import com.financetracker.infrastructure.adapters.outbound.splitwise.dto.Expense
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SplitwiseController(val splitwiseApplicationService: SplitwiseApplicationService) {

  @PostMapping("/sync-splitwise-month")
  fun syncSplitwiseMonth(
      @RequestBody request: FetchMonthSplitwiseTransactionsCommand
  ): ResponseEntity<List<Expense>> {
    try {
      splitwiseApplicationService.syncMonthExpenses(request)
      return ResponseEntity.ok().build()
    } catch (e: Exception) {
      return ResponseEntity.internalServerError().build()
    }
  }

  @PostMapping("/sync-splitwise")
  fun syncSplitwise(
      @RequestBody request: SyncSplitwiseTransactions
  ): ResponseEntity<List<Expense>> {
    try {
      return ResponseEntity.ok().body(splitwiseApplicationService.syncExpenses(request))
    } catch (e: Exception) {
      return ResponseEntity.internalServerError().build()
    }
  }
}
