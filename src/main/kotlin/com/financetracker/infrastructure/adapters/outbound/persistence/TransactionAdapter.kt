package com.financetracker.infrastructure.adapters.outbound.persistence

import com.financetracker.application.ports.output.TransactionPersistence
import com.financetracker.domain.model.Account
import com.financetracker.domain.model.Transaction
import com.financetracker.domain.model.TransactionType
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.AccountEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.CategoryRepository
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.TransactionRepository
import com.financetracker.infrastructure.adapters.outbound.persistence.utils.toEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.utils.toModel
import com.financetracker.infrastructure.adapters.outbound.persistence.utils.updateFromModel
import java.time.LocalDate
import java.time.YearMonth
import java.util.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class TransactionAdapter(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : TransactionPersistence {
  override fun save(transaction: Transaction): UUID {
    val categoryEntity =
        transaction.category?.let { category ->
          categoryRepository.findById(category.id!!).orElseThrow {
            NoSuchElementException("Category not found: ${category.id}")
          }
        }

    val transactionEntity = transaction.toEntity(categoryEntity)
    transactionEntity.apply { personalShare = amount }
    return transactionRepository.save(transactionEntity).id
  }

  override fun update(transaction: Transaction): UUID {
    val existingEntity =
        transactionRepository.findById(transaction.id!!).orElseThrow {
          NoSuchElementException("Transaction not found: ${transaction.id}")
        }

    val categoryEntity =
        transaction.category?.let { category ->
          categoryRepository.findById(category.id!!).orElseThrow {
            NoSuchElementException("Category not found: ${category.id}")
          }
        }

    if (transaction.amount > 0.0) {
      existingEntity.amount = transaction.amount
    }

    existingEntity.updateFromModel(transaction)

    existingEntity.apply { category = categoryEntity ?: category }

    return transactionRepository.save(existingEntity).id
  }

  override fun findByAccountInAndTypeAndIsDeletedAndOccurredOnBetween(
      accounts: List<Account>,
      type: TransactionType,
      isDeleted: Boolean,
      startDate: LocalDate,
      endDate: LocalDate
  ): List<Transaction> {
    return transactionRepository
        .findByAccountInAndTypeAndOccurredOnBetween(
            accounts = accounts.map { AccountEntity().apply { id = it.id!! } },
            type = type,
            startDate = startDate,
            endDate = endDate)
        .filter { it.isDeleted == isDeleted }
        .map { it.toModel() }
  }

  override fun findByAccountInAndOccurredOnBetween(
      accounts: List<Account>,
      startDate: LocalDate,
      endDate: LocalDate
  ): List<Transaction> {
    return transactionRepository
        .findByAccountInAndOccurredOnBetween(
            accounts = accounts.map { AccountEntity().apply { id = it.id!! } },
            startDate = startDate,
            endDate = endDate)
        .map { it.toModel() }
  }

  override fun getSavingsBetween(yearMonth: YearMonth, accounts: List<UUID>): Double {
    return transactionRepository.getSavingsTotalForMonth(
        yearMonth.year, yearMonth.month.value, accounts)
  }

  override fun getIncomeBetween(yearMonth: YearMonth, accounts: List<UUID>): Double {
    return transactionRepository.getSIncomeTotalForMonth(
        yearMonth.year, yearMonth.month.value, accounts)
  }
}
