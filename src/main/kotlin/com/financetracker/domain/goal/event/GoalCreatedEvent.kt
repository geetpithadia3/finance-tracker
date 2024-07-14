package com.financetracker.domain.goal.event

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.time.LocalDate
import java.util.*

data class GoalCreatedEvent(
    @TargetAggregateIdentifier val goalId: UUID,
    val name: String,
    val description: String,
    val targetDate: LocalDate,
    val amountTarget: Double,
    val amountProgress: Double
)

data class GoalProgressUpdated(
    @TargetAggregateIdentifier val goalId: UUID,
    val amount: Double,
    val updatedOn: LocalDate
)
