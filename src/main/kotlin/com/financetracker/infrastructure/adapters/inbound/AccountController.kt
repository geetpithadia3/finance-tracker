package com.financetracker.infrastructure.adapters.inbound

import com.financetracker.application.AccountApplicationService
import com.financetracker.application.queries.AccountBalancesQuery
import com.financetracker.application.queries.TransactionsForMonthQuery
import com.financetracker.domain.account.projections.AccountBalanceView
import com.financetracker.domain.account.projections.MonthTransactionsView
import com.financetracker.infrastructure.adapters.inbound.dto.CreateAccountRequest
import com.financetracker.infrastructure.adapters.inbound.dto.CreditAccountRequest
import com.financetracker.infrastructure.adapters.inbound.dto.DebitAccountRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class AccountController(val accountApplicationService: AccountApplicationService) {

  @PostMapping("/account")
  fun create(@RequestBody request: CreateAccountRequest): ResponseEntity<AccountBalanceView> {
    return ResponseEntity.ok(accountApplicationService.createAccount(request))
  }

  @PostMapping("/account/{accountId}/credit")
  fun credit(@PathVariable accountId: String, @RequestBody request: CreditAccountRequest) {
    accountApplicationService.creditAccount(accountId, request)
  }

  @PostMapping("/account/{accountId}/debit")
  fun debit(@PathVariable accountId: String, @RequestBody request: DebitAccountRequest) {
    accountApplicationService.debitAccount(accountId, request)
  }

  @GetMapping("/account")
  fun balances(): ResponseEntity<List<AccountBalanceView>> {
    return ResponseEntity.ok(accountApplicationService.getAccounts(AccountBalancesQuery()))
  }

  @GetMapping("/transactions")
  fun transactions(
      @RequestBody request: TransactionsForMonthQuery
  ): ResponseEntity<List<MonthTransactionsView>> {
    return ResponseEntity.ok(accountApplicationService.getTransactions(request))
  }
}
