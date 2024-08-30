package com.financetracker.infrastructure.adapters.outbound.persistence.entity.goal

import com.financetracker.domain.model.PayFrequency
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import jakarta.persistence.*
import java.time.LocalDate
import java.util.*

@Entity
class PayScheduleEntity {
  @Id @GeneratedValue(strategy = GenerationType.UUID) var id: UUID? = null

  lateinit var startDate: LocalDate

  @Enumerated lateinit var frequency: PayFrequency

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id") lateinit var user: UserEntity
}
