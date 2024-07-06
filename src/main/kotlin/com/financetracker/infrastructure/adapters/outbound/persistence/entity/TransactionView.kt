package com.financetracker.infrastructure.adapters.outbound.persistence.entity

import com.financetracker.domain.account.model.Category
import com.financetracker.domain.account.model.TransactionType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import java.time.LocalDate

@Entity
class TransactionView {

  @Id lateinit var id: String

  @Enumerated(EnumType.STRING) lateinit var type: TransactionType

  @Enumerated(EnumType.STRING) lateinit var category: Category

  lateinit var description: String

  var amount: Double = 0.0

  lateinit var occurredOn: LocalDate

  var deleted: Boolean = false
}
