package com.financetracker.infrastructure.adapters.inbound.controller

import com.financetracker.application.AccountApplicationService
import com.financetracker.application.api.dto.*
import com.financetracker.domain.account.model.Account
import com.financetracker.domain.account.projections.AccountBalanceProjection
import com.financetracker.domain.account.projections.AccountTransactionsProjection
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class AccountController(private val accountApplicationService: AccountApplicationService) {
  @PostMapping("/account")
  fun createAccount(@RequestBody request: CreateAccountRequest): ResponseEntity<Account> {
    val account = accountApplicationService.createAccount(request)
    return ResponseEntity.ok(account)
  }

  @PostMapping("/account/{account_id}/credit")
  fun credit(
      @PathVariable("account_id") accountId: String,
      @RequestBody request: TransactionRequest
  ): ResponseEntity<Unit> {
    val creditRequest =
        CreditTransactionRequest(
            account = UUID.fromString(accountId),
            amount = request.amount,
            description = request.description,
            category = request.category,
            occurredOn = request.occurredOn)
    accountApplicationService.credit(creditRequest)
    return ResponseEntity.ok().body(Unit)
  }

  @PostMapping("/account/{account_id}/debit")
  fun debit(
      @PathVariable("account_id") accountId: String,
      @RequestBody request: TransactionRequest
  ): ResponseEntity<Unit> {
    val debitRequest =
        DebitTransactionRequest(
            account = UUID.fromString(accountId),
            amount = request.amount,
            description = request.description,
            category = request.category,
            occurredOn = request.occurredOn)
    accountApplicationService.debit(debitRequest)
    return ResponseEntity.ok().body(Unit)
  }

  @GetMapping("/account/{account_id}/balance")
  fun getBalance(
      @PathVariable("account_id") accountId: String,
  ): ResponseEntity<AccountBalanceProjection> {
    val request = AccountBalanceRequest(UUID.fromString(accountId))
    return ResponseEntity.ok().body(accountApplicationService.queryAccountBalance(request))
  }

  @GetMapping("/account/{account_id}/transactions")
  fun getTransactions(
      @PathVariable("account_id") accountId: String,
  ): ResponseEntity<AccountTransactionsProjection> {
    val request = AccountTransactionRequest(UUID.fromString(accountId))
    return ResponseEntity.ok().body(accountApplicationService.queryAccountTransactions(request))
  }
}
