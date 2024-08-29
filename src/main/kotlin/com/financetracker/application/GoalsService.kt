package com.financetracker.application

import com.financetracker.domain.goal.projection.GoalView
import com.financetracker.infrastructure.adapters.inbound.dto.AddGoalRequest
import com.financetracker.infrastructure.adapters.inbound.dto.PayScheduleRequest
import com.financetracker.infrastructure.adapters.inbound.dto.UpdateGoalRequest
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.goal.Goal
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.goal.PaySchedule
import com.financetracker.infrastructure.adapters.outbound.persistence.respository.GoalRepository
import com.financetracker.infrastructure.adapters.outbound.persistence.respository.goal.PayScheduleRepository
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.queryhandling.QueryGateway
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class GoalsService(
    private val goalRepository: GoalRepository,
    private val payScheduleRepository: PayScheduleRepository,
    private val commandGateway: CommandGateway,
    private val queryGateway: QueryGateway
) {

  fun addGoal(request: AddGoalRequest, user: UserEntity) {
    val goal =
        Goal().apply {
          id = UUID.randomUUID()
          name = request.name
          description = request.description
          targetDate = request.targetDate
          amountProgress = request.amountProgress
          amountTarget = request.amountTarget
          this.user = user
        }
    goalRepository.save(goal)
  }

  fun updateGoal(request: UpdateGoalRequest, user: UserEntity) {
    val goal = goalRepository.findByIdAndUser(request.goalId, user)
    goal?.let {
      it.amountProgress += request.amount
      goalRepository.save(it)
    }
  }

  fun list(user: UserEntity): List<GoalView> {
    return goalRepository.findByUser(user).map { goal ->
      GoalView(
          id = goal.id,
          name = goal.name,
          description = goal.description,
          amountProgress = goal.amountProgress,
          amountTarget = goal.amountTarget,
          targetDate = goal.targetDate,
          amountPerPayPeriod =
              calculateSavingsPerPayPeriod(
                  goal.targetDate, goal.amountTarget, goal.amountProgress, user))
    }
  }

  fun addPaySchedule(request: PayScheduleRequest, user: UserEntity): PaySchedule {
    return payScheduleRepository.save(
        PaySchedule().apply {
          startDate = request.startDate
          frequency = request.frequency
          this.user = user
        })
  }

  private fun calculateSavingsPerPayPeriod(
      targetDate: LocalDate,
      amountTarget: Double,
      amountProgress: Double,
      user: UserEntity
  ): Double {
    val paySchedule = payScheduleRepository.findByUser(user).firstOrNull()
    var frequencyInWeeks = 4
    var startDate = LocalDate.now()
    if (paySchedule != null) {
      frequencyInWeeks = paySchedule.frequency
      startDate = paySchedule.startDate
    }

    var nextPayDate = startDate
    while (nextPayDate.isBefore(LocalDate.now())) {
      nextPayDate = nextPayDate.plusWeeks(frequencyInWeeks.toLong())
    }

    val remainingAmount = amountTarget - amountProgress
    val weeksUntilTarget = targetDate.toEpochDay() - LocalDate.now().toEpochDay() / 7
    val numberOfPayPeriods = weeksUntilTarget / frequencyInWeeks

    return remainingAmount / numberOfPayPeriods
  }
}
