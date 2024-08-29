package com.financetracker.domain.account.projections

import com.financetracker.application.dto.response.AccountBalanceResponse
import com.financetracker.application.queries.TransactionsForMonthQuery
import com.financetracker.application.queries.account.AccountBalancesQuery
import com.financetracker.application.queries.account.AccountListQuery
import com.financetracker.domain.account.events.AccountCreatedEvent
import com.financetracker.domain.account.events.TransactionAddedEvent
import com.financetracker.domain.account.model.TransactionType
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.AccountEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.TransactionEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.respository.AccountRepository
import com.financetracker.infrastructure.adapters.outbound.persistence.respository.TransactionRepository
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrElse

@Component
class AccountProjection(
    val accountRepository: AccountRepository,
    val transactionRepository: TransactionRepository
) {

  @EventHandler
  fun on(event: AccountCreatedEvent) {
    val accountBalance =
        AccountEntity().apply {
          id = event.accountId
          balance = event.initialBalance.value
          org = event.org.toString()
          type = event.type.toString()
        }
    accountRepository.save(accountBalance)
  }

  //  @EventHandler
  //  fun on(event: TransactionDeletedEvent) {
  //    val account =
  //        accountBalanceRepository.findByAccountId(event.accountId).getOrElse {
  //          throw RuntimeException()
  //        }
  //
  //    when (event.type) {
  //      TransactionType.DEBIT -> accountBalance.balance += event.amount.value
  //      TransactionType.CREDIT -> accountBalance.balance -= event.amount.value
  //    }
  //
  //    val transaction = transactionRepository.findById(event.transactionId).get()
  //    transaction.deleted = true
  //    transactionRepository.save(transaction)
  //
  //    accountBalanceRepository.save(accountBalance)
  //  }
  //
  @EventHandler
  fun on(event: TransactionAddedEvent) {
    val accountBalance =
        accountRepository.findById(event.accountId).getOrElse { throw RuntimeException() }

    when (event.type) {
      TransactionType.DEBIT -> accountBalance.balance -= event.amount.value
      TransactionType.CREDIT -> accountBalance.balance += event.amount.value
    }
    accountBalance.transactions.add(
        TransactionEntity().apply {
          id = event.transactionId
          type = event.type
          category = event.details.category
          description = event.details.description
          amount = event.amount.value
          occurredOn = event.details.occurredOn
        })

    accountRepository.save(accountBalance)
  }

  @QueryHandler
  fun getBalances(query: AccountBalancesQuery): List<AccountBalanceResponse> {
    val balanceList = accountRepository.findAll()
    return balanceList.map { AccountBalanceResponse(it.id, it.balance) }
  }

  @QueryHandler
  fun getAccounts(query: AccountListQuery): List<AccountView> {
    val accountList = accountRepository.findAll()
    return accountList.map {
      AccountView(accountId = it.id, org = it.org, type = it.type, balance = it.balance)
    }
  }

  @QueryHandler
  fun getTransactions(query: TransactionsForMonthQuery): List<MonthTransactionsView> {
    val accountList = accountRepository.findAll()
    return accountList
        .map { account ->
          account.transactions
              .filter {
                it.occurredOn.year == query.year.value &&
                    it.occurredOn.month == query.month &&
                    it.type == TransactionType.DEBIT &&
                    !it.deleted
              }
              .map {
                MonthTransactionsView(
                    id = it.id,
                    account = account.id,
                    amount = it.amount,
                    description = it.description,
                    category = it.category,
                    type = it.type,
                    date = it.occurredOn)
              }
        }
        .flatten()
  }
}
