package com.financetracker.infrastructure.adapters.outbound.persistence.entity.goal

import com.financetracker.domain.model.PayFrequency
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import jakarta.persistence.*
import java.time.LocalDate

@Entity
class PayScheduleEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null

  lateinit var startDate: LocalDate

  @Enumerated lateinit var frequency: PayFrequency

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id") lateinit var user: UserEntity
}
