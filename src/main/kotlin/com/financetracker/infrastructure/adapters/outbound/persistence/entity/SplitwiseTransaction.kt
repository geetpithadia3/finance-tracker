package com.financetracker.infrastructure.adapters.outbound.persistence.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDate

@Entity
class SplitwiseTransaction {
  @Id var id: Long = 0
  lateinit var updatedOn: LocalDate
}
