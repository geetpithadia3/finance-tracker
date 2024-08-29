package com.financetracker.infrastructure.adapters.outbound.persistence.respository.goal

import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.goal.PaySchedule
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PayScheduleRepository : JpaRepository<PaySchedule, Long> {
  fun findByUser(user: UserEntity): List<PaySchedule>
}
