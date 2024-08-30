package com.financetracker.infrastructure.adapters.outbound.persistence

import com.financetracker.application.ports.output.GoalPersistence
import com.financetracker.domain.model.Goal
import com.financetracker.domain.model.GoalProgressUpdate
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.GoalEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.GoalProgressUpdateEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.GoalRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class GoalAdapter(private val goalRepository: GoalRepository) : GoalPersistence {

  override fun save(goal: Goal): Goal {
    val goalEntity = mapToGoalEntity(goal)
    val savedGoalEntity = goalRepository.save(goalEntity)
    return mapToDomainGoal(savedGoalEntity)
  }

  override fun findByIdAndUserId(id: UUID, userId: Long): Goal? {
    return goalRepository.findByIdAndUserId(id, userId)?.let { mapToDomainGoal(it) }
  }

  override fun findAllByUserId(userId: Long): List<Goal> {
    return goalRepository.findAllByUserId(userId).map { mapToDomainGoal(it) }
  }

  private fun mapToGoalEntity(goal: Goal): GoalEntity {
    return GoalEntity(
        id = goal.id,
        name = goal.name,
        description = goal.description,
        targetDate = goal.targetDate,
        amountTarget = goal.amountTarget,
        amountProgress = goal.amountProgress,
        userId = goal.userId,
        progressUpdates =
            goal.progressUpdates.map { mapToGoalProgressUpdateEntity(it) }.toMutableList())
  }

  private fun mapToGoalProgressUpdateEntity(update: GoalProgressUpdate): GoalProgressUpdateEntity {
    return GoalProgressUpdateEntity(
        id = update.id, amount = update.amount, updatedOn = update.updatedOn)
  }

  private fun mapToDomainGoal(goalEntity: GoalEntity): Goal {
    return Goal(
        id = goalEntity.id,
        name = goalEntity.name,
        description = goalEntity.description,
        targetDate = goalEntity.targetDate,
        amountTarget = goalEntity.amountTarget,
        amountProgress = goalEntity.amountProgress,
        userId = goalEntity.userId,
        toSavePerPayPeriod = 0.0,
        progressUpdates =
            goalEntity.progressUpdates.map { mapToDomainGoalProgressUpdate(it) }.toMutableList())
  }

  private fun mapToDomainGoalProgressUpdate(
      updateEntity: GoalProgressUpdateEntity
  ): GoalProgressUpdate {
    return GoalProgressUpdate(
        id = updateEntity.id, amount = updateEntity.amount, updatedOn = updateEntity.updatedOn)
  }
}
