package com.financetracker.infrastructure.adapters.outbound.persistence.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(
    name = "categories", uniqueConstraints = [UniqueConstraint(columnNames = ["name", "user_id"])])
class CategoryEntity {
  @Id @GeneratedValue(strategy = GenerationType.UUID) lateinit var id: UUID

  @Column(nullable = false) lateinit var name: String

  var isActive: Boolean = true

  var isEditable: Boolean = true

  @ManyToOne @JoinColumn(name = "user_id") lateinit var user: UserEntity
}
