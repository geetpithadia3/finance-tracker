package com.financetracker.domain.model

import com.financetracker.infrastructure.adapters.outbound.persistence.entity.AccountEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.TransactionEntity
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class Transaction(
    val id: UUID? = null,
    val type: TransactionType? = null,
    val subType: TransactionSubType? = null,
    val category: Category? = null,
    val description: String? = null,
    val amount: Double = 0.0,
    val occurredOn: LocalDate? = null,
    val accountId: UUID,
    val externalId: String? = null,
    val lastSyncedAt: LocalDateTime? = null,
    val isDeleted: Boolean = false,
    val linkedTransaction: Transaction? = null
)

fun Transaction.toEntity(): TransactionEntity {
  return TransactionEntity().apply {
    type = this@toEntity.type ?: type
    subType = this@toEntity.subType ?: subType
    category = this@toEntity.category?.toEntity()!!
    description = this@toEntity.description ?: description
    amount = this@toEntity.amount
    externalId = this@toEntity.externalId ?: externalId
    occurredOn = this@toEntity.occurredOn ?: occurredOn
    linkedTransaction = this@toEntity.linkedTransaction?.toEntity()
    lastSyncedOn = (this@toEntity.lastSyncedAt ?: lastSyncedAt)!!
    account = AccountEntity().apply { id = this@toEntity.accountId }
  }
}
