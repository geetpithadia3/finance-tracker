package com.financetracker.application.ports.output

import com.financetracker.domain.model.Goal
import java.util.*

interface GoalPersistence {
  fun save(goal: Goal): Goal

  fun findByIdAndUserId(id: UUID, userId: Long): Goal?

  fun findAllByUserId(userId: Long): List<Goal>
}
