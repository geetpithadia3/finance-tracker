package com.financetracker.application.commands

import com.financetracker.domain.account.model.TransactionType
import com.financetracker.domain.account.valueObjects.Money
import com.financetracker.domain.account.valueObjects.TransactionDetails
import org.axonframework.modelling.command.TargetAggregateIdentifier

data class AddTransactionCommand(
    @TargetAggregateIdentifier val accountId: String,
    val type: TransactionType,
    val amount: Money,
    val details: TransactionDetails
)
