package com.financetracker.infrastructure.adapters.outbound.persistence.entity.goal

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDate
import java.util.*

@Entity
class GoalUpdate {

  @Id lateinit var id: UUID

  var amount: Double = 0.0

  lateinit var updatedOn: LocalDate
}
