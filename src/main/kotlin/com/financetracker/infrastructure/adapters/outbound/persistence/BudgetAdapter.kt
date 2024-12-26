package com.financetracker.infrastructure.adapters.outbound.persistence

import com.financetracker.application.ports.output.BudgetPersistence
import com.financetracker.domain.model.Budget
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.BudgetEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.CategoryBudgetEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.toModel
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.BudgetRepository
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.CategoryRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.time.YearMonth

@Repository
class BudgetAdapter(
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository
) : BudgetPersistence {

  override fun save(budget: Budget): Budget {
    val userEntity = UserEntity().apply { id = budget.userId }

    val budgetEntity =
        BudgetEntity().apply {
          user = userEntity
          yearMonth = budget.yearMonth
          isActive = budget.isActive
        }

    budget.categoryLimits.forEach { categoryBudget ->
      val categoryEntity = categoryRepository.getReferenceById(categoryBudget.categoryId)
      val categoryBudgetEntity =
          CategoryBudgetEntity().apply {
            this.budget = budgetEntity
            this.category = categoryEntity
            this.budgetAmount = categoryBudget.budgetAmount
          }
      budgetEntity.categoryLimits.add(categoryBudgetEntity)
    }

    return budgetRepository.save(budgetEntity).toModel()
  }

  override fun findByUserAndYearMonth(user: User, yearMonth: YearMonth): Budget? {
    val userEntity = UserEntity().apply { id = user.id!! }
    return budgetRepository.findByUserAndYearMonth(userEntity, yearMonth)?.toModel()
  }

  override fun findByUser(user: User): List<Budget> {
    val userEntity = UserEntity().apply { id = user.id!! }
    return budgetRepository.findByUser(userEntity).map { it.toModel() }
  }

  override fun update(budget: Budget): Budget {
    val existingBudget =
        budgetRepository.findById(budget.id!!).orElseThrow {
          NoSuchElementException("Budget not found")
        }

    existingBudget.apply {
      isActive = budget.isActive
      updatedAt = LocalDateTime.now()

      // Remove orphaned category limits
      categoryLimits.removeIf { existingLimit ->
        !budget.categoryLimits.any { it.categoryId == existingLimit.category.id }
      }

      // Update existing and add new category limits
      budget.categoryLimits.forEach { categoryBudget ->
        val categoryEntity = categoryRepository.getReferenceById(categoryBudget.categoryId)

        // Find existing limit or create new one
        val categoryBudgetEntity =
            categoryLimits.find { it.category.id == categoryBudget.categoryId }
                ?: CategoryBudgetEntity().apply {
                  this.budget = existingBudget
                  this.category = categoryEntity
                  categoryLimits.add(this)
                }

        // Update the limit value
        categoryBudgetEntity.budgetAmount = categoryBudget.budgetAmount
      }
    }

    return budgetRepository.save(existingBudget).toModel()
  }

  override fun findLatestBeforeYearMonth(user: User, yearMonth: YearMonth): Budget? {
    val userEntity = UserEntity().apply { id = user.id!! }
    return budgetRepository
        .findFirstByUserAndYearMonthBeforeOrderByYearMonthDesc(userEntity, yearMonth)
        ?.toModel()
  }
}
