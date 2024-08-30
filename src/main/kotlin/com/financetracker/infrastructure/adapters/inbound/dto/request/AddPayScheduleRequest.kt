package com.financetracker.infrastructure.adapters.inbound.dto.request

import com.financetracker.domain.model.PayFrequency
import java.time.LocalDate

data class AddPayScheduleRequest(val startDate: LocalDate, val frequency: PayFrequency)
