package com.financetracker.domain.account.events

import com.financetracker.domain.account.model.AccountType
import com.financetracker.domain.account.model.Organization
import com.financetracker.domain.account.valueObjects.Money

data class AccountCreatedEvent(
    val accountId: String,
    val type: AccountType,
    val org: Organization,
    val initialBalance: Money
)
