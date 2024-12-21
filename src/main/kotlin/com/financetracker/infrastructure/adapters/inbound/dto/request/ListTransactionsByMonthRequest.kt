package com.financetracker.infrastructure.adapters.inbound.dto.request

import java.time.YearMonth

data class ListTransactionsByMonthRequest(val yearMonth: YearMonth)
