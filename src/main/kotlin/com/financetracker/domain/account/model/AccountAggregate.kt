package com.financetracker.domain.account.model

import com.financetracker.application.commands.account.AddTransactionCommand
import com.financetracker.application.commands.account.CreateAccountCommand
import com.financetracker.application.commands.account.DeleteTransactionCommand
import com.financetracker.domain.account.events.AccountCreatedEvent
import com.financetracker.domain.account.events.TransactionAddedEvent
import com.financetracker.domain.account.events.TransactionDeletedEvent
import com.financetracker.domain.account.valueObjects.Money
import com.financetracker.domain.account.valueObjects.minus
import com.financetracker.domain.account.valueObjects.plus
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate

@Aggregate(snapshotTriggerDefinition = "snapshotTriggerDefinition")
class AccountAggregate {
  @AggregateIdentifier private lateinit var accountId: String
  lateinit var type: AccountType
  lateinit var org: Organization
  lateinit var balance: Money
  var transactions: MutableList<String> = mutableListOf()

  constructor()

  @CommandHandler
  constructor(command: CreateAccountCommand) {
    AggregateLifecycle.apply(
        AccountCreatedEvent(command.accountId, command.type, command.org, command.initialBalance))
  }

  @CommandHandler
  fun handle(command: AddTransactionCommand) {
    AggregateLifecycle.apply(
        TransactionAddedEvent(
            command.accountId,
            command.transactionId,
            command.type,
            command.amount,
            command.details))
  }

  @CommandHandler
  fun handle(command: DeleteTransactionCommand) {
    AggregateLifecycle.apply(
        TransactionDeletedEvent(
            command.accountId, command.transactionId, command.type, command.amount))
  }

  @EventSourcingHandler
  fun on(event: AccountCreatedEvent) {
    this.accountId = event.accountId
    this.balance = event.initialBalance
    this.org = event.org
    this.type = event.type
  }

  @EventSourcingHandler
  fun on(event: TransactionDeletedEvent) {
    when (event.type) {
      TransactionType.DEBIT -> this.balance += event.amount
      TransactionType.CREDIT -> this.balance -= event.amount
    }
    transactions.remove(event.transactionId)
  }

  @EventSourcingHandler
  fun on(event: TransactionAddedEvent) {
    when (event.type) {
      TransactionType.DEBIT -> this.balance -= event.amount
      TransactionType.CREDIT -> this.balance += event.amount
    }
    transactions.add(event.transactionId)
  }
}
