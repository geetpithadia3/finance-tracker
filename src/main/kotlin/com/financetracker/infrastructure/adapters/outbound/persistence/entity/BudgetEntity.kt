package com.financetracker.infrastructure.adapters.outbound.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.YearMonth
import java.util.*

@Entity
@Table(name = "budgets")
class BudgetEntity {
  @Id @GeneratedValue(strategy = GenerationType.UUID) var id: UUID? = null

  @ManyToOne @JoinColumn(name = "user_id", nullable = false) lateinit var user: UserEntity

  @Column(nullable = false) lateinit var yearMonth: YearMonth

  @OneToMany(mappedBy = "budget", cascade = [CascadeType.ALL], orphanRemoval = true)
  var categoryLimits: MutableList<CategoryBudgetEntity> = mutableListOf()

  @Column(nullable = false) var isActive: Boolean = true

  @Column(nullable = false) var createdAt: LocalDateTime = LocalDateTime.now()

  @Column(nullable = false) var updatedAt: LocalDateTime = LocalDateTime.now()
}

@Entity
@Table(name = "category_budgets")
class CategoryBudgetEntity {
  @Id @GeneratedValue(strategy = GenerationType.UUID) var id: UUID? = null

  @ManyToOne @JoinColumn(name = "budget_id", nullable = false) lateinit var budget: BudgetEntity

  @ManyToOne
  @JoinColumn(name = "category_id", nullable = false)
  lateinit var category: CategoryEntity

  @Column(nullable = false) var budgetAmount: Double = 0.0
}
