package com.financetracker.infrastructure.adapters.outbound.persistence.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDate
import java.util.*

@Entity
class PaySchedule() {
  @Id @GeneratedValue(strategy = GenerationType.UUID) lateinit var id: UUID
  lateinit var startDate: LocalDate
  var frequency: Int = 1
}
