package com.financetracker.domain.account.events

import java.time.LocalDate
import java.util.*

data class AccountCredited(
    override var id: UUID,
    val amount: Double,
    val description: String,
    val category: String,
    val occurredOn: LocalDate
) : AccountEvent(id)
