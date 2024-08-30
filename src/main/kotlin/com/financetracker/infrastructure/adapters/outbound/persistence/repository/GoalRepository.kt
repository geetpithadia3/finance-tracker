package com.financetracker.infrastructure.adapters.outbound.persistence.repository

import com.financetracker.infrastructure.adapters.outbound.persistence.entity.GoalEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface GoalRepository : JpaRepository<GoalEntity, UUID> {
  fun findByIdAndUserId(id: UUID, userId: UUID): GoalEntity?

  fun findAllByUserId(userId: UUID): List<GoalEntity>
}
