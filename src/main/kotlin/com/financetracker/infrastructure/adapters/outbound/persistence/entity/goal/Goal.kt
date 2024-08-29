package com.financetracker.infrastructure.adapters.outbound.persistence.entity.goal

import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import jakarta.persistence.*
import java.time.LocalDate
import java.util.*

@Entity
class Goal {
  @Id @GeneratedValue(strategy = GenerationType.UUID) lateinit var id: UUID

  lateinit var name: String

  lateinit var description: String

  lateinit var targetDate: LocalDate

  var amountProgress: Double = 0.0

  var amountTarget: Double = 0.0

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id") lateinit var user: UserEntity
}
