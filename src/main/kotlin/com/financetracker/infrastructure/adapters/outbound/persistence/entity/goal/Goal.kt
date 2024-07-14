package com.financetracker.infrastructure.adapters.outbound.persistence.entity.goal

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import java.time.LocalDate
import java.util.*

@Entity
class Goal {
  @Id lateinit var id: UUID
  var amountProgress: Double = 0.0
  var amountTarget: Double = 0.0
  lateinit var targetDate: LocalDate
  lateinit var name: String
  lateinit var description: String
  @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
  var updates: MutableList<GoalUpdate> = mutableListOf()
}
