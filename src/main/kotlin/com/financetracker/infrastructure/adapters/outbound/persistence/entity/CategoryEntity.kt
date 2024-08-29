package com.financetracker.infrastructure.adapters.outbound.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "categories")
class CategoryEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null

  @Column(unique = true) lateinit var name: String
}
