package com.financetracker.infrastructure.adapters.outbound.persistence.entity

import com.financetracker.domain.model.TransactionSubType
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

  @Enumerated(EnumType.STRING) lateinit var subType: TransactionSubType

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "category_id")
  var category: CategoryEntity? = null

  lateinit var description: String

  var amount: Double = 0.0

  lateinit var occurredOn: LocalDate

  var externalId: String? = null

  lateinit var lastSyncedOn: LocalDateTime

  var isDeleted: Boolean = false

  @OneToOne(cascade = [CascadeType.PERSIST]) var linkedTransaction: TransactionEntity? = null

  @ManyToOne @JoinColumn(name = "account_id", nullable = false) lateinit var account: AccountEntity
}
