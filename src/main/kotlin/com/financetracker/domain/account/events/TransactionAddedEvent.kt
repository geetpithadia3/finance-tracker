package com.financetracker.domain.account.events

import com.financetracker.domain.account.model.TransactionType
import com.financetracker.domain.account.valueObjects.Money
import com.financetracker.domain.account.valueObjects.TransactionDetails
import org.axonframework.modelling.command.TargetAggregateIdentifier

data class TransactionAddedEvent(
    @TargetAggregateIdentifier val accountId: String,
    val transactionId: String,
    val type: TransactionType,
    val amount: Money,
    val details: TransactionDetails
)

data class TransactionDeletedEvent(
    @TargetAggregateIdentifier val accountId: String,
    val transactionId: String,
    val type: TransactionType,
    val amount: Money,
)
