package com.financetracker.infrastructure.adapters.outbound.persistence.repository

import com.financetracker.infrastructure.adapters.outbound.persistence.entity.IncomeSourceEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface IncomeSourceRepository : JpaRepository<IncomeSourceEntity, UUID> {
  fun findByUser(user: UserEntity): List<IncomeSourceEntity>

  fun findByUserAndIsDeletedFalse(user: UserEntity): List<IncomeSourceEntity>
}