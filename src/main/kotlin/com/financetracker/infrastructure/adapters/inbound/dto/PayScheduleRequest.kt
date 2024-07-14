package com.financetracker.infrastructure.adapters.inbound.dto

import java.time.LocalDate

data class PayScheduleRequest(val startDate: LocalDate, val frequency: Int)
