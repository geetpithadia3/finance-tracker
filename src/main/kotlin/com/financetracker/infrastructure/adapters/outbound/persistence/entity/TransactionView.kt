package com.financetracker.infrastructure.adapters.outbound.persistence.entity

import com.financetracker.application.commands.TransactionType
import com.financetracker.domain.account.model.Category
import jakarta.persistence.*
import java.time.LocalDate

@Entity
class TransactionView {

  @Id @GeneratedValue(strategy = GenerationType.UUID) lateinit var id: String

  @Enumerated(EnumType.STRING) lateinit var type: TransactionType

  @Enumerated(EnumType.STRING) lateinit var category: Category

  lateinit var description: String

  var amount: Double = 0.0

  lateinit var occurredOn: LocalDate
}
