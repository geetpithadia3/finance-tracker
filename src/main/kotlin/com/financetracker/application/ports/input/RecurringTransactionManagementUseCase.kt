package com.financetracker.application.ports.input

import com.financetracker.domain.model.Transaction
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.RecurrenceRequest
import com.financetracker.infrastructure.adapters.inbound.dto.response.RecurringTransactionResponse
import java.time.YearMonth
import java.util.*

interface RecurringTransactionManagementUseCase {
    fun createOrUpdateFromTransaction(
        transactionId: UUID,
        recurrenceRequest: RecurrenceRequest,
        user: User
    ): UUID
    
    fun getProjectedTransactions(yearMonth: YearMonth, user: User): List<Transaction>
    
    fun findByAccountsAndActive(user: User): List<RecurringTransactionResponse>
    
    fun findById(id: UUID, user: User): RecurringTransactionResponse?
    
    fun delete(id: UUID, user: User)
    
    /**
     * Updates the active status of a recurring transaction
     * @param id The ID of the recurring transaction
     * @param isActive The new active status
     * @param user The authenticated user
     * @return The updated recurring transaction
     */
    fun updateStatus(id: UUID, isActive: Boolean, user: User): RecurringTransactionResponse
} 