package com.financetracker.infrastructure.adapters.outbound.persistence.entity.account

import jakarta.persistence.*

@Entity
class Account {
  @Id lateinit var id: String

  @Column lateinit var type: String

  @Column lateinit var org: String

  @Column var balance: Double = 0.0

  @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
  var transactions: MutableList<Transaction> = mutableListOf()
}
