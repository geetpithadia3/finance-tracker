package com.financetracker.domain.account.projections

import com.financetracker.application.queries.AccountBalancesQuery
import com.financetracker.application.queries.TransactionsForMonthQuery
import com.financetracker.domain.account.events.AccountCreatedEvent
import com.financetracker.domain.account.events.TransactionAddedEvent
import com.financetracker.domain.account.events.TransactionDeletedEvent
import com.financetracker.domain.account.model.TransactionType
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.AccountBalance
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.TransactionView
import com.financetracker.infrastructure.adapters.outbound.persistence.respository.AccountBalanceRepository
import com.financetracker.infrastructure.adapters.outbound.persistence.respository.TransactionRepository
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrElse

@Component
class AccountBalanceProjection(
    val accountBalanceRepository: AccountBalanceRepository,
    val transactionRepository: TransactionRepository
) {

  @EventHandler
  fun on(event: AccountCreatedEvent) {
    val accountBalance =
        AccountBalance().apply {
          accountId = event.accountId
          balance = event.initialBalance.value
        }
    accountBalanceRepository.save(accountBalance)
  }

  @EventHandler
  fun on(event: TransactionDeletedEvent) {
    val accountBalance =
        accountBalanceRepository.findByAccountId(event.accountId).getOrElse {
          throw RuntimeException()
        }

    when (event.type) {
      TransactionType.DEBIT -> accountBalance.balance += event.amount.value
      TransactionType.CREDIT -> accountBalance.balance -= event.amount.value
    }

    val transaction = transactionRepository.findById(event.transactionId).get()
    transaction.deleted = true
    transactionRepository.save(transaction)

    accountBalanceRepository.save(accountBalance)
  }

  @EventHandler
  fun on(event: TransactionAddedEvent) {
    val accountBalance =
        accountBalanceRepository.findByAccountId(event.accountId).getOrElse {
          throw RuntimeException()
        }

    when (event.type) {
      TransactionType.DEBIT -> accountBalance.balance -= event.amount.value
      TransactionType.CREDIT -> accountBalance.balance += event.amount.value
    }
    accountBalance.transactions.add(
        TransactionView().apply {
          id = event.transactionId
          type = event.type
          category = event.details.category
          description = event.details.description
          amount = event.amount.value
          occurredOn = event.details.occurredOn
        })

    accountBalanceRepository.save(accountBalance)
  }

  @QueryHandler
  fun getBalances(query: AccountBalancesQuery): List<AccountBalanceView> {
    val balanceList = accountBalanceRepository.findAll()
    return balanceList.map { AccountBalanceView(it.accountId, it.balance) }
  }

  @QueryHandler
  fun getTransactions(query: TransactionsForMonthQuery): List<MonthTransactionsView> {
    val accountList = accountBalanceRepository.findAll()
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
                    account = account.accountId,
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
