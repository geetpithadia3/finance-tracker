package com.financetracker.application.ports.output

import com.financetracker.domain.model.PaySchedule
import com.financetracker.domain.model.User
import java.util.*

interface PaySchedulePersistence {
  fun save(paySchedule: PaySchedule): UUID

  fun get(user: User): PaySchedule?
}
