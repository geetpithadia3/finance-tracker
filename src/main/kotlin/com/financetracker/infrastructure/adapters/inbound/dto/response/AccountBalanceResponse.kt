package com.financetracker.infrastructure.adapters.inbound.dto.response

import java.util.*

data class AccountBalanceResponse(
    val accountId: UUID,
    val name: String,
    var org: String,
    var type: String,
    var balance: Double
)
