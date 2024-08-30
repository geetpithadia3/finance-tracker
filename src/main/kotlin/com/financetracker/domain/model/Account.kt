package com.financetracker.domain.model

import java.util.*

data class Account(
    var id: UUID? = null,
    val name: String,
    var type: String,
    var org: String,
    var balance: Double = 0.0,
    var userId: UUID,
    var transactions: MutableList<Transaction> = mutableListOf()
)
