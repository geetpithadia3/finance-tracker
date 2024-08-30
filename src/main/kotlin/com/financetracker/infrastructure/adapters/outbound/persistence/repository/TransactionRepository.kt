package com.financetracker.infrastructure.adapters.outbound.persistence.repository

import com.financetracker.domain.model.TransactionType
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.AccountEntity
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

  fun findByAccountInAndTypeAndOccurredOnBetween(
      accounts: List<AccountEntity>,
      type: TransactionType,
      startDate: LocalDate,
      endDate: LocalDate
  ): List<TransactionEntity>

  fun findByExternalId(externalId: String): TransactionEntity?
}