package com.financetracker.domain.account.projections

data class AccountView(
    val accountId: String,
    var org: String,
    val type: String,
    var balance: Double
)
