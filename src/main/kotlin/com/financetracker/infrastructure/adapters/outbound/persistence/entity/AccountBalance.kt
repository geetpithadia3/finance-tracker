package com.financetracker.infrastructure.adapters.outbound.persistence.entity

import jakarta.persistence.*

@Entity
class AccountBalance {
  @Id lateinit var accountId: String

  @Column var balance: Double = 0.0

  @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
  var transactions: MutableList<TransactionView> = mutableListOf()
}
