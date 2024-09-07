package com.financetracker.infrastructure.adapters.outbound.persistence.entity

import com.financetracker.infrastructure.adapters.outbound.persistence.entity.goal.PayScheduleEntity
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "user_entity")
class UserEntity {
  @Id @GeneratedValue(strategy = GenerationType.UUID) lateinit var id: UUID

  @Column(unique = true) lateinit var username: String

  lateinit var password: String

  var externalId: String? = null

  var externalKey: String? = null

  @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
  var accounts: MutableList<AccountEntity> = mutableListOf()

  @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
  var paySchedules: MutableList<PayScheduleEntity> = mutableListOf()
}
