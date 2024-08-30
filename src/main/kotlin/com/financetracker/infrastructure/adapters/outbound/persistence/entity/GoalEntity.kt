package com.financetracker.infrastructure.adapters.outbound.persistence.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.util.*

@Entity
@Table(name = "goals")
class GoalEntity {
  @Id @GeneratedValue(strategy = GenerationType.UUID) lateinit var id: UUID

  @Column(nullable = false) lateinit var name: String

  @Column(nullable = false) lateinit var description: String

  @Column(nullable = false) lateinit var targetDate: LocalDate

  @Column(nullable = false) var amountTarget: Double = 0.0

  @Column(nullable = false) var amountProgress: Double = 0.0

  @ManyToOne @JoinColumn(name = "user_id") lateinit var user: UserEntity

  @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
  @JoinColumn(name = "goal_id")
  var progressUpdates: MutableList<GoalProgressUpdateEntity> = mutableListOf()
}

@Entity
@Table(name = "goal_progress_updates")
class GoalProgressUpdateEntity(
    @Id var id: UUID = UUID.randomUUID(),
    @Column(nullable = false) var amount: Double,
    @Column(nullable = false) var updatedOn: LocalDate
)
