package com.financetracker.infrastructure.adapters.outbound.persistence.entity

import com.financetracker.infrastructure.adapters.outbound.persistence.entity.goal.PaySchedule
import jakarta.persistence.*

@Entity
@Table(name = "users")
class UserEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long = 0

  @Column(unique = true) lateinit var username: String

  lateinit var password: String

  var externalId: String? = null

  var externalKey: String? = null

  @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
  var accounts: MutableList<AccountEntity> = mutableListOf()

  @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
  var paySchedules: MutableList<PaySchedule> = mutableListOf()
}
