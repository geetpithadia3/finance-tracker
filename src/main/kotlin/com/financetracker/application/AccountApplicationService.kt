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
import com.financetracker.infrastructure.adapters.inbound.dto.AddExpenseRequest
import com.financetracker.infrastructure.adapters.inbound.dto.CreateAccountRequest
import com.financetracker.infrastructure.adapters.inbound.dto.CreditAccountRequest
import com.financetracker.infrastructure.adapters.inbound.dto.DebitAccountRequest
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.stereotype.Service

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

  fun creditAccount(accountId: String, request: CreditAccountRequest) {
    commandGateway.send<AddTransactionCommand>(
        AddTransactionCommand(
            accountId = accountId,
            type = TransactionType.CREDIT,
            amount = Money(request.amount, Currency.CAD),
            details =
                TransactionDetails(
                    description = request.description,
                    category = Category.valueOf(request.category.uppercase()),
                    occurredOn = request.occurredOn)))
  }

  fun debitAccount(accountId: String, request: DebitAccountRequest) {
    commandGateway.send<AddTransactionCommand>(
        AddTransactionCommand(
            accountId = accountId,
            type = TransactionType.DEBIT,
            amount = Money(request.amount, Currency.CAD),
            details =
                TransactionDetails(
                    description = request.description,
                    category = Category.valueOf(request.category.uppercase()),
                    occurredOn = request.occurredOn)))
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

  fun addTransactions(request: List<AddExpenseRequest>): List<AddExpenseRequest> {
    request
        .map {
          AddTransactionCommand(
              accountId = it.accountId,
              type = TransactionType.DEBIT,
              amount = Money(it.amount, Currency.CAD),
              details =
                  TransactionDetails(
                      description = it.description,
                      category = Category.valueOf(it.category.uppercase()),
                      occurredOn = it.occurredOn))
        }
        .forEach { commandGateway.send<AddTransactionCommand>(it) }
    return request
  }

  fun listCategories(): List<String> {
    return Category.entries.map { it.toString() }
  }
}
