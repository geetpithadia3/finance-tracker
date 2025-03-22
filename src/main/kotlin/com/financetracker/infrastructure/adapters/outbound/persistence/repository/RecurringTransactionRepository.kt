package com.financetracker.infrastructure.adapters.outbound.persistence.repository

import com.financetracker.infrastructure.adapters.outbound.persistence.entity.AccountEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.RecurringTransactionEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

@Repository
interface RecurringTransactionRepository : JpaRepository<RecurringTransactionEntity, UUID> {
    fun findByAccountInAndIsActiveTrue(accounts: List<AccountEntity>): List<RecurringTransactionEntity>
    
    fun findByAccountInAndIsActiveTrueAndStartDateLessThanEqual(
        accounts: List<AccountEntity>, 
        date: LocalDate
    ): List<RecurringTransactionEntity>
    
    @Query("""
        SELECT rt FROM RecurringTransactionEntity rt
        WHERE rt.account IN :accounts
        AND rt.isActive = true
        AND (rt.endDate IS NULL OR rt.endDate >= :date)
    """)
    fun findActiveRecurringTransactions(
        accounts: List<AccountEntity>,
        date: LocalDate
    ): List<RecurringTransactionEntity>
} 