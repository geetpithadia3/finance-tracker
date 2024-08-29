package com.financetracker.domain.goal.projection

import com.financetracker.application.queries.goal.GoalListQuery
import com.financetracker.domain.goal.event.GoalCreatedEvent
import com.financetracker.domain.goal.event.GoalProgressUpdated
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.goal.Goal
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.goal.GoalUpdate
import com.financetracker.infrastructure.adapters.outbound.persistence.respository.GoalRepository
import com.financetracker.infrastructure.adapters.outbound.persistence.respository.goal.PayScheduleRepository
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.*
import kotlin.jvm.optionals.getOrElse

@Component
class GoalProjection(
    val goalRepository: GoalRepository,
    val payScheduleRepository: PayScheduleRepository
) {

  @EventHandler
  fun on(event: GoalCreatedEvent) {
    val goal =
        Goal().apply {
          id = event.goalId
          name = event.name
          description = event.description
          targetDate = event.targetDate
          amountProgress = event.amountProgress
          amountTarget = event.amountTarget
        }
    goalRepository.save(goal)
  }

  @EventHandler
  fun on(event: GoalProgressUpdated) {
    val goal = goalRepository.findById(event.goalId).getOrElse { throw RuntimeException() }

    goal.amountProgress += event.amount

    goal.updates.add(
        GoalUpdate().apply {
          id = UUID.randomUUID()
          amount = event.amount
          updatedOn = event.updatedOn
        })

    goalRepository.save(goal)
  }

  @QueryHandler
  fun getAccounts(query: GoalListQuery): List<GoalView> {
    val goalList = goalRepository.findAll()
    return goalList.map {
      GoalView(
          id = it.id,
          name = it.name,
          description = it.description,
          amountProgress = it.amountProgress,
          amountTarget = it.amountTarget,
          targetDate = it.targetDate,
          amountPerPayPeriod =
              calculateSavingsPerPayPeriod(it.targetDate, it.amountTarget, it.amountProgress))
    }
  }

  fun calculateSavingsPerPayPeriod(
      targetDate: LocalDate,
      amountTarget: Double,
      amountProgress: Double
  ): Double {
    val paySchedule = payScheduleRepository.findAll()
    var frequencyInWeeks = 4
    var startDate = LocalDate.now()
    if (paySchedule.size > 0) {
      frequencyInWeeks = paySchedule.first().frequency
      startDate = paySchedule.first().startDate
    }

    var nextPayDate = startDate
    while (nextPayDate.isBefore(LocalDate.now())) {
      nextPayDate = nextPayDate.plusWeeks(frequencyInWeeks.toLong())
    }

    var payPeriodsRemaining = 0
    while (nextPayDate.isBefore(targetDate) || nextPayDate.isEqual(targetDate)) {
      payPeriodsRemaining++
      nextPayDate = nextPayDate.plusWeeks(frequencyInWeeks.toLong())
    }

    // Calculate the remaining amount to be saved
    val remainingAmount = amountTarget - amountProgress

    // Calculate savings per pay period
    val amountToSave =
        if (payPeriodsRemaining > 0) {
          remainingAmount / payPeriodsRemaining
        } else {
          remainingAmount
        }

    return amountToSave
  }
}
