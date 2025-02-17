package com.financetracker.infrastructure.adapters.outbound.persistence.entity

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
}
