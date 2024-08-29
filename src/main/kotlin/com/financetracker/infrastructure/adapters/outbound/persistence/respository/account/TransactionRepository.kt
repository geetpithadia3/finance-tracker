package com.financetracker.infrastructure.adapters.outbound.persistence.respository.account

import com.financetracker.infrastructure.adapters.outbound.persistence.entity.TransactionEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface TransactionRepository : JpaRepository<TransactionEntity, String> {
  fun findByAccountUserAndOccurredOnBetween(
      user: UserEntity,
      startDate: LocalDate,
      endDate: LocalDate
  ): List<TransactionEntity>
}
