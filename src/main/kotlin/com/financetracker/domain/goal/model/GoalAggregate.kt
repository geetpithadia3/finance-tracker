package com.financetracker.domain.goal.model

import com.financetracker.application.commands.goal.CreateGoalCommand
import com.financetracker.application.commands.goal.UpdateGoalProgressCommand
import com.financetracker.domain.goal.event.GoalCreatedEvent
import com.financetracker.domain.goal.event.GoalProgressUpdated
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import java.time.LocalDate
import java.util.*

@Aggregate(snapshotTriggerDefinition = "snapshotTriggerDefinition")
class GoalAggregate {
  @AggregateIdentifier private lateinit var goalId: UUID
  lateinit var name: String
  lateinit var description: String
  lateinit var targetDate: LocalDate
  var amountProgress: Double = 0.0
  var amountTarget: Double = 0.0

  constructor()

  @CommandHandler
  constructor(command: CreateGoalCommand) {
    AggregateLifecycle.apply(
        GoalCreatedEvent(
            command.goalId,
            command.name,
            command.description,
            command.targetDate,
            command.amountTarget,
            command.amountProgress))
  }

  @CommandHandler
  fun handle(command: UpdateGoalProgressCommand) {
    AggregateLifecycle.apply(GoalProgressUpdated(command.goalId, command.amount, command.updatedOn))
  }

  @EventSourcingHandler
  fun on(event: GoalCreatedEvent) {
    this.goalId = event.goalId
    this.name = event.name
    this.description = event.description
    this.targetDate = event.targetDate
    this.amountTarget = event.amountTarget
  }

  @EventSourcingHandler
  fun on(event: GoalProgressUpdated) {
    this.amountProgress += event.amount
  }
}
