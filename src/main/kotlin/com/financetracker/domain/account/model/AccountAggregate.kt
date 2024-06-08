package com.financetracker.domain.account.model

import com.financetracker.application.commands.AddTransactionCommand
import com.financetracker.application.commands.CreateAccountCommand
import com.financetracker.domain.account.events.AccountCreatedEvent
import com.financetracker.domain.account.events.TransactionAddedEvent
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

  constructor()

  @CommandHandler
  constructor(command: CreateAccountCommand) {
    AggregateLifecycle.apply(
        AccountCreatedEvent(command.accountId, command.type, command.org, command.initialBalance))
  }

  @CommandHandler
  fun handle(command: AddTransactionCommand) {
    AggregateLifecycle.apply(
        TransactionAddedEvent(command.accountId, command.type, command.amount, command.details))
  }

  @EventSourcingHandler
  fun on(event: AccountCreatedEvent) {
    this.accountId = event.accountId
    this.balance = event.initialBalance
    this.org = event.org
    this.type = event.type
  }

  @EventSourcingHandler
  fun on(event: TransactionAddedEvent) {
    when (event.type) {
      TransactionType.DEBIT -> this.balance -= event.amount
      TransactionType.CREDIT -> this.balance += event.amount
    }
  }
}
