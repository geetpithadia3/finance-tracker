package com.financetracker.application

import com.financetracker.application.commands.goal.CreateGoalCommand
import com.financetracker.application.commands.goal.UpdateGoalProgressCommand
import com.financetracker.application.queries.goal.GoalListQuery
import com.financetracker.domain.goal.projection.GoalView
import com.financetracker.infrastructure.adapters.inbound.dto.AddGoalRequest
import com.financetracker.infrastructure.adapters.inbound.dto.PayScheduleRequest
import com.financetracker.infrastructure.adapters.inbound.dto.UpdateGoalRequest
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.PaySchedule
import com.financetracker.infrastructure.adapters.outbound.persistence.respository.PayScheduleRepository
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.stereotype.Service
import java.util.*

@Service
class GoalsService(
    val commandGateway: CommandGateway,
    val queryGateway: QueryGateway,
    val payScheduleRepository: PayScheduleRepository
) {

  fun addGoal(request: AddGoalRequest) {
    val id = UUID.randomUUID()
    commandGateway.send<CreateGoalCommand>(
        CreateGoalCommand(
            goalId = id,
            name = request.name,
            description = request.description,
            targetDate = request.targetDate,
            amountProgress = request.amountProgress,
            amountTarget = request.amountTarget))
  }

  fun updateGoal(request: UpdateGoalRequest) {
    commandGateway.send<UpdateGoalProgressCommand>(
        UpdateGoalProgressCommand(
            goalId = request.goalId, amount = request.amount, updatedOn = request.updatedOn))
  }

  fun list(): List<GoalView> {
    return queryGateway
        .query(GoalListQuery(), ResponseTypes.multipleInstancesOf(GoalView::class.java))
        .get()
  }

  fun addPaySchedule(request: PayScheduleRequest): PaySchedule {
    return payScheduleRepository.save(
        PaySchedule().apply {
          startDate = request.startDate
          frequency = request.frequency
        })
  }
}
