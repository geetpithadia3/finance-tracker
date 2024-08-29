package com.financetracker.domain.model

data class Account(
    var id: String,
    var type: String,
    var org: String,
    var balance: Double = 0.0,
    var user: Long,
    var transactions: MutableList<Transaction> = mutableListOf()
)
