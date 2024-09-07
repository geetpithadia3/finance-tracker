package com.financetracker.infrastructure.adapters.outbound.persistence.entity

import com.financetracker.domain.model.Category
import com.financetracker.domain.model.TransactionType
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "transaction_entity")
class TransactionEntity {

  @GeneratedValue(strategy = GenerationType.UUID) @Id lateinit var id: UUID

  @Enumerated(EnumType.STRING) lateinit var type: TransactionType

  @Enumerated(EnumType.STRING) lateinit var category: Category

  lateinit var description: String

  var amount: Double = 0.0

  lateinit var occurredOn: LocalDate

  var externalId: String? = null

  lateinit var lastSyncedOn: LocalDateTime

  @ManyToOne @JoinColumn(name = "account_id", nullable = false) lateinit var account: AccountEntity
}
