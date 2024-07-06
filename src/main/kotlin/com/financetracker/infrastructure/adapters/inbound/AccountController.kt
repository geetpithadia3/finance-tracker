package com.financetracker.infrastructure.adapters.inbound

import com.financetracker.application.AccountApplicationService
import com.financetracker.application.queries.AccountBalancesQuery
import com.financetracker.domain.account.projections.AccountBalanceView
import com.financetracker.infrastructure.adapters.inbound.dto.AddTransactionRequest
import com.financetracker.infrastructure.adapters.inbound.dto.CreateAccountRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountController(val accountApplicationService: AccountApplicationService) {

  @PostMapping("/account")
  fun create(@RequestBody request: CreateAccountRequest): ResponseEntity<AccountBalanceView> {
    return ResponseEntity.ok(accountApplicationService.createAccount(request))
  }

  @GetMapping("/account")
  fun balances(): ResponseEntity<List<AccountBalanceView>> {
    return ResponseEntity.ok(accountApplicationService.getAccounts(AccountBalancesQuery()))
  }

  @PostMapping("/transactions")
  fun addTransactions(@RequestBody request: List<AddTransactionRequest>): ResponseEntity<Unit> {
    accountApplicationService.addTransactions(request)
    return ResponseEntity.ok().build()
  }

  @GetMapping("/categories")
  fun listCategories(): ResponseEntity<List<String>> {
    return ResponseEntity.ok(accountApplicationService.listCategories())
  }
}
