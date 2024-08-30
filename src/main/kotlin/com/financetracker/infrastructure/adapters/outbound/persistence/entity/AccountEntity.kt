package com.financetracker.infrastructure.adapters.outbound.persistence.entity

import jakarta.persistence.*
import java.util.*

@Entity
class AccountEntity {
  @Id @GeneratedValue(strategy = GenerationType.UUID) lateinit var id: UUID

  @Column(unique = true) lateinit var name: String

  @Column lateinit var type: String

  @Column lateinit var org: String

  @Column var balance: Double = 0.0

  @ManyToOne @JoinColumn(name = "user_id") lateinit var user: UserEntity

  @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "account")
  var transactions: MutableList<TransactionEntity> = mutableListOf()
}
