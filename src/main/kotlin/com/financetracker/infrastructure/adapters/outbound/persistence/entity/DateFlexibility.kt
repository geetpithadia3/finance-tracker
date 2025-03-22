package com.financetracker.infrastructure.adapters.outbound.persistence.entity

enum class DateFlexibility {
    EXACT,
    EARLY_MONTH,
    MID_MONTH,
    LATE_MONTH,
    CUSTOM_RANGE,
    WEEKDAY,
    WEEKEND,
    MONTH_RANGE,
    SEASON
  }