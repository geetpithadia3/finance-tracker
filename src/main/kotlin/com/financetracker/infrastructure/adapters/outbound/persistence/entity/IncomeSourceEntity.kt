package com.financetracker.infrastructure.adapters.outbound.persistence.entity

import com.financetracker.domain.model.PayFrequency
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "income_source")
class IncomeSourceEntity {
  @Id @GeneratedValue(strategy = GenerationType.UUID) var id: UUID? = null

  @ManyToOne @JoinColumn(name = "user_id", nullable = false) lateinit var user: UserEntity

  @Enumerated(EnumType.STRING) @Column(nullable = false) lateinit var payFrequency: PayFrequency

  @Column(nullable = false) var payAmount: Double = 0.0

  @Column(nullable = false) lateinit var nextPayDate: LocalDateTime

  @Column(nullable = false) var isDeleted: Boolean = false
}

fun IncomeSourceEntity.toModel() =
    com.financetracker.domain.model.IncomeSource(
        id = id,
        userId = user.id,
        payFrequency = payFrequency,
        payAmount = payAmount,
        nextPayDate = nextPayDate,
        isDeleted = isDeleted)
