package com.financetracker.infrastructure.adapters.outbound.persistence.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDate

@Entity
class ExternalTransactionEntity {
  @Id lateinit var id: String
  var amount: Double = 0.0
  lateinit var updatedOn: LocalDate
}
