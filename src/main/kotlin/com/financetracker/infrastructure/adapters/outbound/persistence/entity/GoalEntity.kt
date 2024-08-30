package com.financetracker.infrastructure.adapters.outbound.persistence.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.util.*

@Entity
@Table(name = "goals")
class GoalEntity(
    @Id var id: UUID = UUID.randomUUID(),
    @Column(nullable = false) var name: String,
    @Column(nullable = false) var description: String,
    @Column(nullable = false) var targetDate: LocalDate,
    @Column(nullable = false) var amountTarget: Double,
    @Column(nullable = false) var amountProgress: Double = 0.0,
    @Column(nullable = false) var userId: Long,
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "goal_id")
    var progressUpdates: MutableList<GoalProgressUpdateEntity> = mutableListOf()
)

@Entity
@Table(name = "goal_progress_updates")
class GoalProgressUpdateEntity(
    @Id var id: UUID = UUID.randomUUID(),
    @Column(nullable = false) var amount: Double,
    @Column(nullable = false) var updatedOn: LocalDate
)
