package com.financetracker.infrastructure.adapters.outbound.persistence.entity

import com.financetracker.domain.account.model.TransactionType
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "transactions")
class TransactionEntity {

  @GeneratedValue(strategy = GenerationType.IDENTITY) @Id var id: Long = 0

  @Enumerated(EnumType.STRING) lateinit var type: TransactionType

  @ManyToOne
  @JoinColumn(name = "category_id", nullable = false)
  lateinit var category: CategoryEntity

  lateinit var description: String

  var amount: Double = 0.0

  lateinit var occurredOn: LocalDate

  lateinit var lastSyncedOn: LocalDateTime

  @ManyToOne @JoinColumn(name = "account_id", nullable = false) lateinit var account: AccountEntity
}
