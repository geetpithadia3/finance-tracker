package com.financetracker.infrastructure.adapters.outbound.persistence.respository.goal

import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.goal.Goal
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface GoalRepository : JpaRepository<Goal, UUID> {
  fun findByUser(user: UserEntity): List<Goal>

  fun findByIdAndUser(id: UUID, user: UserEntity): Goal?
}
