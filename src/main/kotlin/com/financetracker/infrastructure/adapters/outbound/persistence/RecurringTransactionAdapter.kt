package com.financetracker.infrastructure.adapters.outbound.persistence

import com.financetracker.application.ports.output.RecurringTransactionPersistence
import com.financetracker.domain.model.Account
import com.financetracker.domain.model.RecurringTransaction
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.AccountEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.CategoryRepository
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.RecurringTransactionRepository
import com.financetracker.infrastructure.adapters.outbound.persistence.utils.toEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.utils.toModel
import jakarta.transaction.Transactional
import java.time.LocalDate
import java.util.*
import org.springframework.stereotype.Service

@Service
@Transactional
class RecurringTransactionAdapter(
    private val recurringTransactionRepository: RecurringTransactionRepository,
    private val categoryRepository: CategoryRepository
) : RecurringTransactionPersistence {

    override fun save(recurringTransaction: RecurringTransaction): UUID {
        val entity = recurringTransaction.toEntity()
        
        // Ensure the category reference is properly set
        val categoryEntity = categoryRepository.findById(recurringTransaction.categoryId).orElseThrow {
            NoSuchElementException("Category not found: ${recurringTransaction.categoryId}")
        }
        entity.category = categoryEntity
        
        return recurringTransactionRepository.save(entity).id
    }

    override fun update(recurringTransaction: RecurringTransaction): UUID {
        val existingEntity = recurringTransactionRepository.findById(recurringTransaction.id!!)
            .orElseThrow { NoSuchElementException("Recurring transaction not found: ${recurringTransaction.id}") }
        
        // Update fields
        existingEntity.apply {
            description = recurringTransaction.description
            amount = recurringTransaction.amount
            
            // Update category if changed
            if (category.id != recurringTransaction.categoryId) {
                val categoryEntity = categoryRepository.findById(recurringTransaction.categoryId).orElseThrow {
                    NoSuchElementException("Category not found: ${recurringTransaction.categoryId}")
                }
                category = categoryEntity
            }
            
            // Update account if changed
            if (account.id != recurringTransaction.accountId) {
                account = AccountEntity().apply { id = recurringTransaction.accountId }
            }
            
            frequency = recurringTransaction.frequency
            startDate = recurringTransaction.startDate
            endDate = recurringTransaction.endDate
            dateFlexibility = recurringTransaction.dateFlexibility
            rangeStart = recurringTransaction.rangeStart
            rangeEnd = recurringTransaction.rangeEnd
            preference = recurringTransaction.preference
            priority = recurringTransaction.priority
            isActive = recurringTransaction.isActive
            lastMatchedTransactionId = recurringTransaction.lastMatchedTransactionId
            isVariableAmount = recurringTransaction.isVariableAmount
            estimatedMinAmount = recurringTransaction.estimatedMinAmount
            estimatedMaxAmount = recurringTransaction.estimatedMaxAmount
        }
        
        return recurringTransactionRepository.save(existingEntity).id
    }

    override fun findById(id: UUID): RecurringTransaction? {
        return recurringTransactionRepository.findById(id).map { it.toModel() }.orElse(null)
    }

    override fun findByAccountsAndActive(accounts: List<Account>): List<RecurringTransaction> {
        val accountEntities = accounts.map { AccountEntity().apply { id = it.id!! } }
        return recurringTransactionRepository.findByAccountInAndIsActiveTrue(accountEntities)
            .map { it.toModel() }
    }

    override fun findActiveRecurringTransactions(accounts: List<Account>, date: LocalDate): List<RecurringTransaction> {
        val accountEntities = accounts.map { AccountEntity().apply { id = it.id!! } }
        return recurringTransactionRepository.findActiveRecurringTransactions(accountEntities, date)
            .map { it.toModel() }
    }

    override fun delete(id: UUID) {
        recurringTransactionRepository.deleteById(id)
    }
} 