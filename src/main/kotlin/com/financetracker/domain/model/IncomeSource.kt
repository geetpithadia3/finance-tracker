package com.financetracker.domain.model

import java.time.LocalDateTime
import java.util.*

data class IncomeSource(
    val id: UUID? = null,
    val userId: UUID,
    val payFrequency: PayFrequency,
    val payAmount: Double,
    val nextPayDate: LocalDateTime,
    val isDeleted: Boolean = false
)

enum class PayFrequency {
  WEEKLY,
  BI_WEEKLY,
  MONTHLY,
  SEMI_MONTHLY;

  fun getNextPayDate(currentDate: LocalDateTime): LocalDateTime {
    return when (this) {
      WEEKLY -> currentDate.plusWeeks(1)
      BI_WEEKLY -> currentDate.plusWeeks(2)
      MONTHLY -> currentDate.plusMonths(1)
      SEMI_MONTHLY ->
          if (currentDate.dayOfMonth <= 15) {
            currentDate.withDayOfMonth(15)
          } else {
            currentDate.plusMonths(1).withDayOfMonth(1)
          }
    }
  }
}
