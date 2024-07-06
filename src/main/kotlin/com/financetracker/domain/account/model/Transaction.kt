package com.financetracker.domain.account.model

import com.financetracker.domain.account.valueObjects.Money

data class Transaction(
    val transactionId: String,
    val type: TransactionType,
    val amount: Money,
    val details: String,
    var deleted: Boolean = false
)
