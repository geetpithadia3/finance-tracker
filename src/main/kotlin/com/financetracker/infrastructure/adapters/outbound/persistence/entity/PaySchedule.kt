package com.financetracker.infrastructure.adapters.outbound.persistence.entity.goal

import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import jakarta.persistence.*
import java.time.LocalDate

@Entity
class PaySchedule {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null

  lateinit var startDate: LocalDate

  var frequency: Int = 0

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id") lateinit var user: UserEntity
}
