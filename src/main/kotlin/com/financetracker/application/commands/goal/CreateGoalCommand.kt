package com.financetracker.application.commands.goal

// import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.time.LocalDate
import java.util.*

data class CreateGoalCommand(
    val goalId: UUID,
    val name: String,
    val description: String,
    val targetDate: LocalDate,
    val amountTarget: Double,
    val amountProgress: Double
)

data class UpdateGoalProgressCommand(
    val goalId: UUID,
    val amount: Double,
    val updatedOn: LocalDate
)
