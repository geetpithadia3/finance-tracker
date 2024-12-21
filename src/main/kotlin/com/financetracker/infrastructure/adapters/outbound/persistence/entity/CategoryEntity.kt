package com.financetracker.infrastructure.adapters.outbound.persistence.entity

import com.financetracker.domain.model.Category
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "categories")
class CategoryEntity {
  @Id @GeneratedValue(strategy = GenerationType.UUID) lateinit var id: UUID

  @Column(unique = true) lateinit var name: String

  var isActive: Boolean = true

  var isEditable: Boolean = true

  @ManyToOne @JoinColumn(name = "user_id") lateinit var user: UserEntity
}

fun CategoryEntity.toModel(): Category {
  return Category(
      id = this.id,
      name = this.name,
      isActive = this.isActive,
      isEditable = this.isEditable,
      userId = this.user.id)
}
