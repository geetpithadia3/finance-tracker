package com.financetracker.application.api.dto

import java.time.Year

data class MonthlyExpenseRequest(val month: Int, val year: Year)
