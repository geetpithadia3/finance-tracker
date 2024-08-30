package com.financetracker.infrastructure.adapters.outbound.persistence.repository

import com.financetracker.infrastructure.adapters.outbound.persistence.entity.GoalEntity
import java.util.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GoalRepository : JpaRepository<GoalEntity, UUID> {
  fun findByIdAndUserId(id: UUID, userId: Long): GoalEntity?

  fun findAllByUserId(userId: Long): List<GoalEntity>
}
