package com.financetracker.domain.model

import java.time.LocalDate
import java.util.*

data class PaySchedule(val startDate: LocalDate, val frequency: PayFrequency, val userId: UUID)

enum class PayFrequency {
  WEEKLY,
  BI_WEEKLY,
  MONTHLY
}
