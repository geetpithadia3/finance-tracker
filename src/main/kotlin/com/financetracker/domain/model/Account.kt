package com.financetracker.domain.model

import com.financetracker.infrastructure.adapters.outbound.persistence.entity.AccountEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import java.util.*

data class Account(
    var id: UUID? = null,
    val name: String,
    var type: String,
    var org: String,
    var balance: Double = 0.0,
    var userId: UUID,
    var transactions: MutableList<Transaction> = mutableListOf()
)

fun Account.toEntity(): AccountEntity {
  val accountEntity =
      AccountEntity().apply {
        name = this@toEntity.name
        type = this@toEntity.type
        org = this@toEntity.org
        balance = this@toEntity.balance
        user = UserEntity().apply { id = this@toEntity.userId }
      }

  if (this@toEntity.id != null) {
    accountEntity.apply { id = this@toEntity.id!! }
  }

  return accountEntity
}
