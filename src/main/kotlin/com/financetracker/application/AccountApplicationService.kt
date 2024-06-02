package com.financetracker.application

import com.financetracker.application.api.AccountQueryUserCase
import com.financetracker.application.api.AccountUseCase
import com.financetracker.application.api.TransactionUseCase
import com.financetracker.application.api.dto.*
import com.financetracker.application.spi.EventStore
import com.financetracker.domain.account.events.AccountCreated
import com.financetracker.domain.account.events.AccountCredited
import com.financetracker.domain.account.events.AccountDebited
import com.financetracker.domain.account.model.Account
import com.financetracker.domain.account.projections.AccountBalanceProjection
import com.financetracker.domain.account.projections.AccountTransactionsProjection
import com.financetracker.domain.account.service.AccountProjectionService
import com.financetracker.domain.account.service.AccountService
import org.springframework.stereotype.Service

@Service
class AccountApplicationService(
    private val accountService: AccountService,
    private val accountProjectionService: AccountProjectionService,
    private val eventStore: EventStore
) : AccountUseCase, TransactionUseCase, AccountQueryUserCase {
  override fun createAccount(createAccountRequest: CreateAccountRequest): Account {
    val createdAccount = accountService.createAccount(createAccountRequest)
    val event =
        AccountCreated(
            createdAccount.id,
            createdAccount.name,
            createdAccount.type.toString(),
            createdAccount.description,
            createdAccount.organization.toString())
    eventStore.save(event)
    return createdAccount
  }

  override fun credit(creditTransactionRequest: CreditTransactionRequest) {
    accountService.validateAccount(creditTransactionRequest.account)
    val event =
        AccountCredited(
            id = creditTransactionRequest.account,
            amount = creditTransactionRequest.amount,
            description = creditTransactionRequest.description,
            category = creditTransactionRequest.category,
            occurredOn = creditTransactionRequest.occurredOn)

    eventStore.save(event)
  }

  override fun debit(debitTransactionRequest: DebitTransactionRequest) {
    accountService.validateAccount(debitTransactionRequest.account)
    val event =
        AccountDebited(
            id = debitTransactionRequest.account,
            amount = debitTransactionRequest.amount,
            description = debitTransactionRequest.description,
            category = debitTransactionRequest.category,
            occurredOn = debitTransactionRequest.occurredOn)

    eventStore.save(event)
  }

  override fun queryAccountBalance(request: AccountBalanceRequest): AccountBalanceProjection {
    val events = eventStore.query(request.accountNumber)
    return accountProjectionService.getAccountBalance(events)
  }

  override fun queryAccountTransactions(
      request: AccountTransactionRequest
  ): AccountTransactionsProjection {
    val events = eventStore.query(request.accountNumber)
    return accountProjectionService.getAccountTransactions(events)
  }
}
