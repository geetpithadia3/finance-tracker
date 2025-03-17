package com.financetracker.application.ports.output

import com.financetracker.domain.model.Account
import com.financetracker.domain.model.RecurringTransaction
import com.financetracker.domain.model.User
import java.time.LocalDate
import java.util.*

interface RecurringTransactionPersistence {
    fun save(recurringTransaction: RecurringTransaction): UUID
    
    fun update(recurringTransaction: RecurringTransaction): UUID
    
    fun findById(id: UUID): RecurringTransaction?
    
    fun findByAccountsAndActive(accounts: List<Account>): List<RecurringTransaction>
    
    fun findActiveRecurringTransactions(accounts: List<Account>, date: LocalDate): List<RecurringTransaction>
    
    fun delete(id: UUID)
} 