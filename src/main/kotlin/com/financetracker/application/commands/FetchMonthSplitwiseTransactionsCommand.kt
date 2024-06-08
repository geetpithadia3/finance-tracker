package com.financetracker.application.commands

import java.time.Month
import java.time.Year

data class FetchMonthSplitwiseTransactionsCommand(
    val month: Month,
    val year: Year,
    val account: String
)
