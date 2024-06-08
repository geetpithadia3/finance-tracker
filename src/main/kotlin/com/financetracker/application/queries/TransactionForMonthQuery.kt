package com.financetracker.application.queries

import java.time.Month
import java.time.Year

data class TransactionsForMonthQuery(val month: Month, val year: Year)
