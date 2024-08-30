package com.financetracker.infrastructure.adapters.outbound.persistence.repository.goal

import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.goal.PayScheduleEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PayScheduleRepository : JpaRepository<PayScheduleEntity, Long> {
  fun findByUser(user: UserEntity): List<PayScheduleEntity>
}
