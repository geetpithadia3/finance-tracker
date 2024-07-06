package com.financetracker.application

import com.financetracker.application.commands.AddTransactionCommand
import com.financetracker.application.commands.CreateAccountCommand
import com.financetracker.application.queries.AccountBalancesQuery
import com.financetracker.application.queries.TransactionsForMonthQuery
import com.financetracker.domain.account.model.AccountType
import com.financetracker.domain.account.model.Category
import com.financetracker.domain.account.model.Organization
import com.financetracker.domain.account.model.TransactionType
import com.financetracker.domain.account.projections.AccountBalanceView
import com.financetracker.domain.account.projections.MonthTransactionsView
import com.financetracker.domain.account.valueObjects.Currency
import com.financetracker.domain.account.valueObjects.Money
import com.financetracker.domain.account.valueObjects.TransactionDetails
import com.financetracker.infrastructure.adapters.inbound.dto.AddTransactionRequest
import com.financetracker.infrastructure.adapters.inbound.dto.CreateAccountRequest
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.stereotype.Service
import java.util.*

@Service
class AccountApplicationService(
    val commandGateway: CommandGateway,
    val queryGateway: QueryGateway
) {

  fun createAccount(request: CreateAccountRequest): AccountBalanceView {
    val accountId = request.org + "_" + request.type
    commandGateway.send<CreateAccountCommand>(
        CreateAccountCommand(
            accountId = accountId,
            type = AccountType.valueOf(request.type.uppercase()),
            org = Organization.valueOf(request.org.uppercase()),
            initialBalance =
                Money(request.initialBalance, Currency.valueOf(request.currency.uppercase()))))

    return AccountBalanceView(accountId, request.initialBalance)
  }

  fun getAccounts(query: AccountBalancesQuery): List<AccountBalanceView> {
    return queryGateway
        .query(query, ResponseTypes.multipleInstancesOf(AccountBalanceView::class.java))
        .get()
  }

  fun getTransactions(query: TransactionsForMonthQuery): List<MonthTransactionsView> {
    return queryGateway
        .query(query, ResponseTypes.multipleInstancesOf(MonthTransactionsView::class.java))
        .get()
  }

  fun addTransactions(request: List<AddTransactionRequest>): List<AddTransactionRequest> {
    request
        .filter { it.type.uppercase() != "TRANSFER" }
        .map {
          AddTransactionCommand(
              accountId = it.accountId,
              transactionId = UUID.randomUUID().toString(),
              type = TransactionType.valueOf(it.type.uppercase()),
              amount = Money(it.amount, Currency.CAD),
              details =
                  TransactionDetails(
                      description = it.description,
                      category = Category.valueOf(it.category.uppercase()),
                      occurredOn = it.occurredOn))
        }
        .forEach { commandGateway.send<AddTransactionCommand>(it) }
    request
        .filter { it.type.uppercase() == "TRANSFER" }
        .forEach {
          commandGateway.send<AddTransactionCommand>(
              AddTransactionCommand(
                  accountId = it.accountId,
                  transactionId = UUID.randomUUID().toString(),
                  type = TransactionType.DEBIT,
                  amount = Money(it.amount, Currency.CAD),
                  details =
                      TransactionDetails(
                          description = "TRANSFER",
                          category = Category.valueOf(it.category.uppercase()),
                          occurredOn = it.occurredOn)))
          commandGateway.send<AddTransactionCommand>(
              AddTransactionCommand(
                  accountId = it.toAccount!!,
                  transactionId = UUID.randomUUID().toString(),
                  type = TransactionType.CREDIT,
                  amount = Money(it.amount, Currency.CAD),
                  details =
                      TransactionDetails(
                          description = "TRANSFER",
                          category = Category.valueOf(it.category.uppercase()),
                          occurredOn = it.occurredOn)))
        }

    return request
  }

  fun listCategories(): List<String> {
    return Category.entries.map { it.toString() }
  }
}
