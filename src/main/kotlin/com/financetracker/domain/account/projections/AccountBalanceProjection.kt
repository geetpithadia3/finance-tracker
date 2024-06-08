package com.financetracker.domain.account.projections

import com.financetracker.application.commands.TransactionType
import com.financetracker.domain.account.events.AccountCreatedEvent
import com.financetracker.domain.account.events.TransactionAddedEvent
import com.financetracker.domain.account.model.Category
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.AccountBalance
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.TransactionView
import com.financetracker.infrastructure.adapters.outbound.persistence.respository.AccountBalanceRepository
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.Month
import java.time.Year
import kotlin.jvm.optionals.getOrElse

@Component
class AccountBalanceProjection(val accountBalanceRepository: AccountBalanceRepository) {

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
          type = event.type
          category = event.details.category
          description = event.details.description
          amount = event.amount.value
          occurredOn = event.details.occurredOn
        })

    accountBalanceRepository.save(accountBalance)
  }

  @QueryHandler
  fun getBalances(query: FindAllAccountBalances): List<AccountBalanceView> {
    val balanceList = accountBalanceRepository.findAll()
    return balanceList.map { AccountBalanceView(it.accountId, it.balance) }
  }

  @QueryHandler
  fun getTransactions(query: QueryTransactionsForMonth): List<MonthTransactionsView> {
    val accountList = accountBalanceRepository.findAll()
    return accountList
        .map { account ->
          account.transactions
              .filter {
                it.occurredOn.year == query.year.value && it.occurredOn.month == query.month
              }
              .map {
                MonthTransactionsView(
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

class FindAllAccountBalances

data class QueryTransactionsForMonth(val month: Month, val year: Year)

data class AccountBalanceView(val accountId: String, var amount: Double)

data class MonthTransactionsView(
    val account: String,
    val amount: Double,
    val description: String,
    val category: Category,
    val type: TransactionType,
    val date: LocalDate
)
