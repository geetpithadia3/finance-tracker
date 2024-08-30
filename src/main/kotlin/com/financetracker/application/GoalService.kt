package com.financetracker.application

import com.financetracker.application.ports.input.GoalManagementUseCase
import com.financetracker.application.ports.output.GoalPersistence
import com.financetracker.application.ports.output.PaySchedulePersistence
import com.financetracker.domain.model.*
import com.financetracker.infrastructure.adapters.inbound.dto.request.CreateGoalRequest
import com.financetracker.infrastructure.adapters.inbound.dto.request.UpdateGoalProgressRequest
import com.financetracker.infrastructure.adapters.inbound.dto.response.GoalProgressUpdateResponse
import com.financetracker.infrastructure.adapters.inbound.dto.response.GoalResponse
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import org.springframework.stereotype.Service

@Service
class GoalService(
    private val goalPersistence: GoalPersistence,
    private val paySchedulePersistence: PaySchedulePersistence
) : GoalManagementUseCase {

  override fun createGoal(request: CreateGoalRequest, user: User): GoalResponse {

    val paySchedule = paySchedulePersistence.get(user)

    val toSavePerPayPeriod =
        calculateToSavePerPayPeriod(
            request.targetDate, request.amountTarget, request.amountProgress, paySchedule)
    val goal =
        Goal(
            name = request.name,
            description = request.description,
            targetDate = request.targetDate,
            amountTarget = request.amountTarget,
            userId = user.id!!,
            toSavePerPayPeriod = toSavePerPayPeriod)
    val savedGoal = goalPersistence.save(goal)
    return mapToGoalResponse(savedGoal)
  }

  override fun updateGoalProgress(request: UpdateGoalProgressRequest, user: User): GoalResponse {
    val goal =
        goalPersistence.findByIdAndUserId(request.goalId, user.id!!)
            ?: throw RuntimeException("Goal not found")

    val progressUpdate = GoalProgressUpdate(amount = request.amount, updatedOn = request.updatedOn)
    goal.progressUpdates.add(progressUpdate)
    goal.amountProgress += request.amount

    val updatedGoal = goalPersistence.save(goal)
    return mapToGoalResponse(updatedGoal)
  }

  override fun listGoals(user: User): List<GoalResponse> {
    return goalPersistence.findAllByUserId(user.id!!).map { mapToGoalResponse(it) }
  }

  private fun calculateToSavePerPayPeriod(
      targetDate: LocalDate,
      amountTarget: Double,
      amountProgress: Double,
      paySchedule: PaySchedule?
  ): Double {
    val remainingAmount = amountTarget - amountProgress
    val totalDays = ChronoUnit.DAYS.between(LocalDate.now(), targetDate)

    val payPeriods =
        when {
          paySchedule != null -> {
            val daysFromStart = ChronoUnit.DAYS.between(paySchedule.startDate, targetDate)
            when (paySchedule.frequency) {
              PayFrequency.WEEKLY -> daysFromStart / 7
              PayFrequency.BI_WEEKLY -> daysFromStart / 14
              PayFrequency.MONTHLY -> daysFromStart / 30
            }
          }
          else -> totalDays / 30
        }

    return remainingAmount / payPeriods
  }

  private fun mapToGoalResponse(goal: Goal): GoalResponse {
    return GoalResponse(
        id = goal.id,
        name = goal.name,
        description = goal.description,
        targetDate = goal.targetDate,
        amountTarget = goal.amountTarget,
        amountProgress = goal.amountProgress,
        progressUpdates =
            goal.progressUpdates.map {
              GoalProgressUpdateResponse(id = it.id, amount = it.amount, updatedOn = it.updatedOn)
            })
  }
}
