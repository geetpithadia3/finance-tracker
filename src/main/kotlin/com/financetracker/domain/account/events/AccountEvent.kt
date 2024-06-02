package com.financetracker.domain.account.events

import java.util.*

open class AccountEvent(open var id: UUID)

data class AccountCreated(
    override var id: UUID,
    val name: String,
    val type: String,
    val description: String,
    val organization: String
) : AccountEvent(id)
