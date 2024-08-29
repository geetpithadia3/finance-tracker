package com.financetracker.domain.goal.model

import com.financetracker.domain.goal.event.GoalCreatedEvent
import com.financetracker.domain.goal.event.GoalProgressUpdatedEvent
import com.financetracker.application.commands.goal.CreateGoalCommand
import com.financetracker.application.commands.goal.UpdateGoalProgressCommand
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import java.util.*

@Aggregate
class GoalAggregate {
    @AggregateIdentifier
    private lateinit var id: UUID
    private lateinit var goal: Goal

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
                command.amountProgress
            )
        )
    }

    @CommandHandler
    fun handle(command: UpdateGoalProgressCommand) {
        AggregateLifecycle.apply(
            GoalProgressUpdatedEvent(
                command.goalId,
                command.amount,
                command.updatedOn
            )
        )
    }

    @EventSourcingHandler
    fun on(event: GoalCreatedEvent) {
        id = event.goalId
        goal = Goal(
            event.goalId,
            event.name,
            event.description,
            event.targetDate,
            event.amountTarget,
            event.amountProgress
        )
    }

    @EventSourcingHandler
    fun on(event: GoalProgressUpdatedEvent) {
        goal.amountProgress += event.amountAdded
    }
}
