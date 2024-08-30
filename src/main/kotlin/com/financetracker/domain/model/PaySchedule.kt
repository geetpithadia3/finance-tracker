package com.financetracker.domain.model

import java.time.LocalDate

data class PaySchedule(val startDate: LocalDate, val frequency: PayFrequency, val user: Long)

enum class PayFrequency {
  WEEKLY,
  BI_WEEKLY,
  MONTHLY
}
