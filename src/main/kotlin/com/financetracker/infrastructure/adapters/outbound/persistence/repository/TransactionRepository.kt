package com.financetracker.infrastructure.adapters.outbound.persistence.repository

import com.financetracker.domain.model.TransactionType
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.AccountEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.TransactionEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
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

  fun findByAccountInAndOccurredOnBetween(
      accounts: List<AccountEntity>,
      startDate: LocalDate,
      endDate: LocalDate
  ): List<TransactionEntity>

  @Query(
      """
        SELECT COALESCE(SUM(t.amount), 0.0)
        FROM TransactionEntity t
        WHERE t.category.name = :categoryName
        AND EXTRACT(YEAR FROM t.occurredOn) = :year
        AND EXTRACT(MONTH FROM t.occurredOn) = :month
        AND t.account.id IN :accounts
        AND t.isDeleted = false
    """)
  fun getTotalAmountByCategoryAndMonth(
      @Param("categoryName") categoryName: String,
      @Param("month") month: Int,
      @Param("year") year: Int,
      @Param("accounts") accounts: List<UUID>
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

  fun findByExternalIdAndAccountId(externalId: String, accountId: UUID): TransactionEntity?

  @Query("SELECT MAX(e.occurredOn) FROM TransactionEntity e where e.account.id IN ?1")
  fun findLastSyncDateForAccount(accountId: UUID): LocalDate?

  @Query(
      """
    SELECT COALESCE(SUM(t.amount), 0.0)
    FROM TransactionEntity t
    JOIN t.category c
    WHERE c.name = 'Savings'
    AND EXTRACT(YEAR FROM t.occurredOn) = :year
    AND EXTRACT(MONTH FROM t.occurredOn) = :month
    AND t.account.id IN :accountIds
    AND t.isDeleted = false
  """)
  fun getSavingsTotalForMonth(
      @Param("year") year: Int,
      @Param("month") month: Int,
      @Param("accountIds") accountIds: List<UUID>
  ): Double


  @Query(
      """
    SELECT COALESCE(SUM(t.amount), 0.0)
    FROM TransactionEntity t
    JOIN t.category c
    WHERE c.name = 'Income'
    AND EXTRACT(YEAR FROM t.occurredOn) = :year
    AND EXTRACT(MONTH FROM t.occurredOn) = :month
    AND t.account.id IN :accountIds
    AND t.isDeleted = false
  """)
  fun getSIncomeTotalForMonth(
      @Param("year") year: Int,
      @Param("month") month: Int,
      @Param("accountIds") accountIds: List<UUID>
  ): Double

  @Query(
      """
    SELECT c.name as category, COALESCE(SUM(t.amount), 0.0) as total
    FROM TransactionEntity t
    JOIN t.category c
    WHERE t.account.id IN :accountIds
    AND EXTRACT(YEAR FROM t.occurredOn) = :year
    AND EXTRACT(MONTH FROM t.occurredOn) = :month
    AND t.isDeleted = false
    AND c.name != 'Savings'
    AND c.name != 'Transfer'
    GROUP BY c.name
  """)
  fun getTotalAmountByCategories(
      @Param("year") year: Int,
      @Param("month") month: Int,
      @Param("accountIds") accountIds: List<UUID>
  ): List<CategoryTotal>

  interface CategoryTotal {
    fun getCategory(): String

    fun getTotal(): Double
  }
}
