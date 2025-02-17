package com.financetracker.infrastructure.adapters.outbound.persistence.utils

import com.financetracker.domain.model.*
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.*
import java.util.*

// User Extensions
fun User.toEntity(): UserEntity {
  val userEntity =
      UserEntity().apply {
        username = this@toEntity.username
        password = this@toEntity.password
      }

  if (this.id != null) {
    userEntity.apply { id = this@toEntity.id }
  }

  return userEntity
}

fun UserEntity.toModel(): User {
  return User(id = this.id, username = this.username, password = this.password)
}

// Account Extensions
fun Account.toEntity(): AccountEntity {
  val accountEntity =
      AccountEntity().apply {
        name = this@toEntity.name
        balance = this@toEntity.balance
        user = UserEntity().apply { id = this@toEntity.userId }
      }

  if (this@toEntity.id != null) accountEntity.apply { id = this@toEntity.id!! }

  return accountEntity
}

fun AccountEntity.toModel(): Account {
  return Account(id = this.id, balance = this.balance, name = this.name, userId = this.user.id)
}

// Budget Extensions
fun Budget.toEntity(): BudgetEntity {
  val userEntity = UserEntity().apply { id = userId }

  return BudgetEntity().apply {
    user = userEntity
    yearMonth = this@toEntity.yearMonth
    isActive = this@toEntity.isActive
  }
}

fun BudgetEntity.toModel(): Budget {
  return Budget(
      id = this.id,
      userId = this.user.id,
      yearMonth = this.yearMonth,
      categoryLimits = this.categoryLimits.map { it.toModel() },
      isActive = this.isActive)
}

fun CategoryBudgetEntity.toModel(): CategoryBudget {
  return CategoryBudget(
      id = this.id, categoryId = this.category.id, budgetAmount = this.budgetAmount)
}

// Category Extensions
fun CategoryEntity.toModel(): Category {
  return Category(
      id = this.id,
      name = this.name,
      isActive = this.isActive,
      isEditable = this.isEditable,
      userId = this.user.id)
}

fun Category.toEntity(): CategoryEntity {
  return CategoryEntity().apply {
    id = this@toEntity.id ?: UUID.randomUUID()
    name = this@toEntity.name
    isActive = this@toEntity.isActive
    isEditable = this@toEntity.isEditable
    user = UserEntity().apply { id = this@toEntity.userId }
  }
}

// Transaction Extensions
fun Transaction.toEntity(categoryEntity: CategoryEntity? = null): TransactionEntity {
  return TransactionEntity().apply {
    type = this@toEntity.type!!
    category = categoryEntity!!
    description = this@toEntity.description!!
    amount = this@toEntity.amount
    occurredOn = this@toEntity.occurredOn!!
    linkedTransaction = this@toEntity.linkedTransaction?.toEntity()
    account = AccountEntity().apply { id = this@toEntity.accountId }
    refunded = this@toEntity.refunded
    personalShare = this@toEntity.personalShare
    owedShare = this@toEntity.owedShare
    shareMetadata = this@toEntity.shareMetadata
  }
}

fun TransactionEntity.updateFromModel(transaction: Transaction) {
  this.apply {
    category = transaction.category?.toEntity() ?: category
    linkedTransaction = transaction.linkedTransaction?.toEntity()
    description = transaction.description ?: description
    occurredOn = transaction.occurredOn ?: occurredOn
    isDeleted = transaction.isDeleted
    refunded = transaction.refunded
    personalShare = transaction.personalShare
    owedShare = transaction.owedShare
    shareMetadata = transaction.shareMetadata
  }
}

fun TransactionEntity.toModel(): Transaction {
  return Transaction(
      id = this.id,
      type = this.type,
      description = this.description,
      occurredOn = this.occurredOn,
      amount = this.amount,
      accountId = this.account.id,
      category = this.category?.toModel(),
      linkedTransaction = this.linkedTransaction?.toModel(),
      refunded = this.refunded,
      personalShare = this.personalShare,
      owedShare = this.owedShare,
      shareMetadata = this.shareMetadata)
}
