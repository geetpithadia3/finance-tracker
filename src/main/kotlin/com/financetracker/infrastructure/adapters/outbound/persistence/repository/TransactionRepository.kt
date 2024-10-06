package com.financetracker.infrastructure.adapters.outbound.persistence.repository

import com.financetracker.domain.model.TransactionType
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.AccountEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.TransactionEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

@Repository
interface TransactionRepository : JpaRepository<TransactionEntity, UUID> {
  fun findByAccountUserAndOccurredOnBetween(
      user: UserEntity,
      startDate: LocalDate,
      endDate: LocalDate
  ): List<TransactionEntity>

  fun findByAccountInAndTypeAndOccurredOnBetween(
      accounts: List<AccountEntity>,
      type: TransactionType,
      startDate: LocalDate,
      endDate: LocalDate
  ): List<TransactionEntity>

  @Query(
      """
        SELECT COALESCE(SUM(t.amount), 0.0)
        FROM TransactionEntity t
        WHERE LOWER(t.category) = LOWER(?1)
        AND EXTRACT(YEAR FROM t.occurredOn) = ?3
        AND EXTRACT(MONTH FROM t.occurredOn) = ?2
        AND t.account.id IN ?4
        AND t.isDeleted = false
    """)
  fun getTotalAmountByCategoryAndMonth(
      category: String,
      month: Int,
      year: Int,
      accounts: List<UUID>
  ): Double

  @Query(
      """
        SELECT COALESCE(SUM(t.amount), 0.0)
        FROM TransactionEntity t
        WHERE LOWER(t.type) = LOWER(?1)
        AND EXTRACT(YEAR FROM t.occurredOn) = ?3
        AND EXTRACT(MONTH FROM t.occurredOn) = ?2
        AND t.account.id IN ?4
        AND t.isDeleted = false
    """)
  fun getTotalAmountByTypeForMonth(
      transactionType: String,
      month: Int,
      year: Int,
      accounts: List<UUID>
  ): Double

  fun findAmountByType(type: TransactionType)

  fun findByExternalId(externalId: String): TransactionEntity?
}
