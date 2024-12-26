package com.financetracker.infrastructure.adapters.outbound.persistence.repository

import com.financetracker.infrastructure.adapters.outbound.persistence.entity.BudgetEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.YearMonth
import java.util.*

@Repository
interface BudgetRepository : JpaRepository<BudgetEntity, UUID> {
  fun findByUserAndYearMonth(user: UserEntity, yearMonth: YearMonth): BudgetEntity?

  fun findByUser(user: UserEntity): List<BudgetEntity>

  @Query(
      """
        SELECT b FROM BudgetEntity b 
        WHERE b.user = :user 
        AND b.yearMonth < :yearMonth 
        ORDER BY b.yearMonth DESC
    """)
  fun findFirstByUserAndYearMonthBeforeOrderByYearMonthDesc(
      user: UserEntity,
      yearMonth: YearMonth
  ): BudgetEntity?
}
