package com.financetracker.application

import com.financetracker.application.ports.input.BudgetManagementUseCase
import com.financetracker.application.ports.output.AccountPersistence
import com.financetracker.application.ports.output.BudgetPersistence
import com.financetracker.application.ports.output.CategoryPersistence
import com.financetracker.application.ports.output.TransactionPersistence
import com.financetracker.domain.model.Budget
import com.financetracker.domain.model.CategoryBudget
import com.financetracker.domain.model.TransactionType
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.CreateBudgetRequest
import com.financetracker.infrastructure.adapters.inbound.dto.request.UpdateBudgetRequest
import com.financetracker.infrastructure.adapters.inbound.dto.response.*
import org.springframework.stereotype.Service
import java.time.YearMonth
import java.util.*

@Service
class BudgetService(
    private val budgetPersistence: BudgetPersistence,
    private val categoryPersistence: CategoryPersistence,
    private val transactionPersistence: TransactionPersistence,
    private val accountPersistence: AccountPersistence,
    private val incomeSourceService: IncomeSourceService
) : BudgetManagementUseCase {

  override fun createBudget(request: CreateBudgetRequest, user: User): BudgetResponse {
    // Validate all categories exist
    request.categoryLimits.forEach { categoryLimit ->
      validateCategory(categoryLimit.categoryId, user)
    }

    val categoryLimits =
        request.categoryLimits.map {
          CategoryBudget(categoryId = it.categoryId, budgetAmount = it.budgetAmount)
        }

    // Add income budget automatically
    val allCategoryLimits = addIncomeBudget(categoryLimits, user)

    val existingBudget = budgetPersistence.findByUserAndYearMonth(user, request.yearMonth)

    return if (existingBudget != null) {
      val updatedBudget = existingBudget.copy(categoryLimits = allCategoryLimits)
      val savedBudget = budgetPersistence.update(updatedBudget)
      mapToBudgetResponse(savedBudget, user)
    } else {
      val budget =
          Budget(
              userId = user.id!!, yearMonth = request.yearMonth, categoryLimits = allCategoryLimits)
      val savedBudget = budgetPersistence.save(budget)
      mapToBudgetResponse(savedBudget, user)
    }
  }

  override fun getBudgetDetails(yearMonth: YearMonth, user: User): BudgetDetailsResponse {
    val budget =
        budgetPersistence.findByUserAndYearMonth(user, yearMonth)
            ?: budgetPersistence.findLatestBeforeYearMonth(user, yearMonth)
    
    if (budget == null) {
        return BudgetDetailsResponse(
            id = UUID.randomUUID(), // Generate temporary ID for response
            yearMonth = yearMonth,
            categories = emptyList()
        )
    }

    val startDate = yearMonth.atDay(1)
    val endDate = yearMonth.atEndOfMonth()
    val accounts = accountPersistence.findByUser(user)

    val excludedCategories = setOf("Transfer")
    val excludedCategoryIds =
        categoryPersistence
            .findByUser(user)
            .filter { it.name in excludedCategories }
            .mapNotNull { it.id }
            .toSet()

    val debitTransactions =
        transactionPersistence
            .findByAccountInAndTypeAndIsDeletedAndOccurredOnBetween(
                accounts, TransactionType.DEBIT, false, startDate, endDate)
            .filter { transaction -> transaction.category?.id !in excludedCategoryIds }

    val creditTransactions =
        transactionPersistence
            .findByAccountInAndTypeAndIsDeletedAndOccurredOnBetween(
                accounts, TransactionType.CREDIT, false, startDate, endDate)
            .filter { transaction -> transaction.category?.id !in excludedCategoryIds }

    val transactions = debitTransactions + creditTransactions
    val categoryExpenses =
        transactions
            .groupBy { it.category?.id }
            .mapValues { it.value.sumOf { transaction -> transaction.amount } }

    val filteredCategoryLimits =
        budget.categoryLimits.filter { categoryBudget ->
          val category = categoryPersistence.findByIdAndUser(categoryBudget.categoryId, user)
          category != null && category.name !in excludedCategories
        }

    return BudgetDetailsResponse(
        id = budget.id!!,
        yearMonth = yearMonth,
        categories =
            filteredCategoryLimits.map { categoryBudget ->
              val spent = categoryExpenses[categoryBudget.categoryId] ?: 0.0

              CategoryBudgetDetailsResponse(
                  categoryId = categoryBudget.categoryId,
                  categoryName =
                      categoryPersistence.findByIdAndUser(categoryBudget.categoryId, user)?.name
                          ?: throw RuntimeException("Category not found"),
                  limit = categoryBudget.budgetAmount,
                  spent = spent)
            })
  }

  override fun updateBudget(id: UUID, request: UpdateBudgetRequest, user: User): BudgetResponse {
    val existingBudget =
        budgetPersistence.findByUserAndYearMonth(user, request.yearMonth)
            ?: throw NoSuchElementException("Budget not found")

    if (existingBudget.id != id) {
      throw IllegalArgumentException("Budget ID mismatch")
    }

    // Validate all categories exist
    request.categoryLimits.forEach { categoryLimit ->
      validateCategory(categoryLimit.categoryId, user)
    }

    val categoryLimits =
        request.categoryLimits.map {
          CategoryBudget(categoryId = it.categoryId, budgetAmount = it.budgetAmount)
        }

    // Add income budget automatically
    val allCategoryLimits = addIncomeBudget(categoryLimits, user)

    val updatedBudget = existingBudget.copy(categoryLimits = allCategoryLimits)

    val savedBudget = budgetPersistence.update(updatedBudget)
    return mapToBudgetResponse(savedBudget, user)
  }

  private fun mapToBudgetResponse(budget: Budget, user: User): BudgetResponse {
    return BudgetResponse(
        id = budget.id!!,
        yearMonth = budget.yearMonth,
        categoryLimits =
            budget.categoryLimits.map {
              CategoryBudgetResponse(
                  categoryId = it.categoryId,
                  categoryName =
                      categoryPersistence.findByIdAndUser(it.categoryId, user)?.name
                          ?: throw RuntimeException("Category not found"),
                  limit = it.budgetAmount)
            })
  }

  private fun validateCategory(categoryId: UUID, user: User) {
    val category =
        categoryPersistence.findByIdAndUser(categoryId, user)
            ?: throw IllegalArgumentException("Category not found: $categoryId")

    if (category.name == "Transfer" || category.name == "Income") {
      throw IllegalArgumentException("Cannot manually set budget for ${category.name} category")
    }
  }

  private fun addIncomeBudget(
      categoryLimits: List<CategoryBudget>,
      user: User
  ): List<CategoryBudget> {
    val incomeCategory =
        categoryPersistence.findByNameAndUser("Income", user)
            ?: throw RuntimeException("Income category not found")

    val monthlyIncome = incomeSourceService.calculateMonthlyIncome(user)

    return categoryLimits +
        CategoryBudget(categoryId = incomeCategory.id!!, budgetAmount = monthlyIncome)
  }

  override fun getBudgetableCategories(user: User): List<CategoryResponse> {
    val excludedCategories = setOf("Transfer", "Income")
    return categoryPersistence
        .findByUser(user)
        .filter { category -> category.isActive && !excludedCategories.contains(category.name) }
        .map { category ->
          CategoryResponse(
              id = category.id!!,
              name = category.name,
              isActive = category.isActive,
              isEditable = category.isEditable)
        }
  }
}
