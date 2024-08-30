package com.financetracker.application.ports.output

import com.financetracker.domain.model.PaySchedule
import com.financetracker.domain.model.User

interface PaySchedulePersistence {
  fun save(paySchedule: PaySchedule): Long

  fun get(user: User): PaySchedule?
}
