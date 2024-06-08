package com.financetracker.domain.account.events

import com.financetracker.application.commands.TransactionType
import com.financetracker.domain.account.valueObjects.Money
import com.financetracker.domain.account.valueObjects.TransactionDetails
import org.axonframework.modelling.command.TargetAggregateIdentifier

data class TransactionAddedEvent(
    @TargetAggregateIdentifier val accountId: String,
    val type: TransactionType,
    val amount: Money,
    val details: TransactionDetails
)
