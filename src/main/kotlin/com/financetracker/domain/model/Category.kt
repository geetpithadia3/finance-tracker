package com.financetracker.domain.model

import com.financetracker.infrastructure.adapters.outbound.persistence.entity.CategoryEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import java.util.*

data class Category(
    val id: UUID? = null,
    val name: String,
    val isActive: Boolean = true,
    val isEditable: Boolean = true,
    val userId: UUID
)

fun Category.toEntity(): CategoryEntity {
  return CategoryEntity().apply {
    id = this@toEntity.id ?: UUID.randomUUID()
    name = this@toEntity.name
    isActive = this@toEntity.isActive
    isEditable = this@toEntity.isEditable
    user = UserEntity().apply { id = this@toEntity.userId }
  }
}
