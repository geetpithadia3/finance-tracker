package com.financetracker.infrastructure.adapters.outbound.persistence.entity

import jakarta.persistence.*

@Entity
class AccountEntity {
  @Id lateinit var id: String

  @Column lateinit var type: String

  @Column lateinit var org: String

  @Column var balance: Double = 0.0

  @ManyToOne @JoinColumn(name = "user_id") lateinit var user: UserEntity

  @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "account")
  var transactions: MutableList<TransactionEntity> = mutableListOf()
}
