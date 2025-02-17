package com.financetracker.application

import com.financetracker.application.ports.input.BudgetManagementUseCase
import com.financetracker.application.ports.output.*
import com.financetracker.domain.model.*
import com.financetracker.infrastructure.adapters.inbound.dto.request.*
import com.financetracker.infrastructure.adapters.inbound.dto.response.*
import java.time.YearMonth
import java.util.*
import org.springframework.stereotype.Service

@Service
class BudgetService(
    private val budgetPersistence: BudgetPersistence,
    private val categoryPersistence: CategoryPersistence,
    private val transactionPersistence: TransactionPersistence,
    private val accountPersistence: AccountPersistence
) : BudgetManagementUseCase {

  override fun createBudget(request: CreateBudgetRequest, user: User): BudgetResponse {
    validateBudgetableCategories(request.categoryLimits, user)
    val categoryLimits = mapToCategoryBudgets(request.categoryLimits)
    val existingBudget = budgetPersistence.findByUserAndYearMonth(user, request.yearMonth)

    return if (existingBudget != null) {
      updateExistingBudget(existingBudget, categoryLimits, user)
    } else {
      createNewBudget(request.yearMonth, categoryLimits, user)
    }
  }

  override fun getBudgetDetails(yearMonth: YearMonth, user: User): BudgetDetailsResponse {
    val budget = findBudget(user, yearMonth)
    return if (budget == null) {
      createEmptyBudgetDetailsResponse(yearMonth)
    } else {
      createBudgetDetailsResponse(budget, user, yearMonth)
    }
  }

  override fun getBudgetableCategories(user: User): List<CategoryResponse> {
    val excludedCategories = setOf(CategoryName.TRANSFER.name, CategoryName.INCOME.name)
    return categoryPersistence
        .findByUser(user)
        .filter { it.isActive && !excludedCategories.contains(it.name) }
        .map { mapToCategoryResponse(it) }
  }

  private fun validateBudgetableCategories(
      categoryLimits: List<CategoryBudgetRequest>,
      user: User
  ) {
    categoryLimits.forEach { validateCategory(it.categoryId, user) }
  }

  private fun validateCategory(categoryId: UUID, user: User) {
    val category =
        categoryPersistence.findByIdAndUser(categoryId, user)
            ?: throw IllegalArgumentException("Category not found: $categoryId")

    if (category.name in setOf(CategoryName.TRANSFER.name, CategoryName.INCOME.name)) {
      throw IllegalArgumentException("Cannot manually set budget for ${category.name} category")
    }
  }

  private fun mapToCategoryBudgets(
      categoryLimits: List<CategoryBudgetRequest>
  ): List<CategoryBudget> {
    return categoryLimits.map { CategoryBudget(null, it.categoryId, it.budgetAmount) }
  }

  private fun updateExistingBudget(
      existingBudget: Budget,
      categoryLimits: List<CategoryBudget>,
      user: User
  ): BudgetResponse {
    val updatedBudget = existingBudget.copy(categoryLimits = categoryLimits)
    val savedBudget = budgetPersistence.update(updatedBudget)
    return mapToBudgetResponse(savedBudget, user)
  }

  /*
   * Create a new budget for the given yearMonth and category limits.
   */
  private fun createNewBudget(
      yearMonth: YearMonth,
      categoryLimits: List<CategoryBudget>,
      user: User
  ): BudgetResponse {
    val budget = Budget(userId = user.id!!, yearMonth = yearMonth, categoryLimits = categoryLimits)
    val savedBudget = budgetPersistence.save(budget)
    return mapToBudgetResponse(savedBudget, user)
  }

  /*
   * Find the budget for the given user and yearMonth. If a budget for the exact yearMonth
   * does not exist, find the latest budget before the given yearMonth.
   */
  private fun findBudget(user: User, yearMonth: YearMonth): Budget? {
    return budgetPersistence.findByUserAndYearMonth(user, yearMonth)
        ?: budgetPersistence.findLatestBeforeYearMonth(user, yearMonth)
  }

  private fun createEmptyBudgetDetailsResponse(yearMonth: YearMonth): BudgetDetailsResponse {
    return BudgetDetailsResponse(
        id = UUID.randomUUID(), yearMonth = yearMonth, categories = emptyList())
  }

  private fun createBudgetDetailsResponse(
      budget: Budget,
      user: User,
      yearMonth: YearMonth
  ): BudgetDetailsResponse {
    val transactions = findTransactions(user, yearMonth)
    val categoryExpenses = calculateCategoryExpenses(transactions)
    val filteredCategoryLimits = filterCategoryLimits(budget.categoryLimits, user)

    return BudgetDetailsResponse(
        id = budget.id!!,
        yearMonth = yearMonth,
        categories =
            mapToCategoryBudgetDetailsResponses(filteredCategoryLimits, categoryExpenses, user))
  }

  private fun findTransactions(user: User, yearMonth: YearMonth): List<Transaction> {
    val startDate = yearMonth.atDay(1)
    val endDate = yearMonth.atEndOfMonth()
    val accounts = accountPersistence.findByUser(user)
    val excludedCategoryIds = findExcludedCategoryIds(user)

    val debitTransactions =
        transactionPersistence
            .findByAccountInAndTypeAndIsDeletedAndOccurredOnBetween(
                accounts, TransactionType.DEBIT, false, startDate, endDate)
            .filter { it.category?.id !in excludedCategoryIds }

    val creditTransactions =
        transactionPersistence
            .findByAccountInAndTypeAndIsDeletedAndOccurredOnBetween(
                accounts, TransactionType.CREDIT, false, startDate, endDate)
            .filter { it.category?.id !in excludedCategoryIds }

    return debitTransactions + creditTransactions
  }

  private fun findExcludedCategoryIds(user: User): Set<UUID> {
    val excludedCategories = setOf("Transfer")
    return categoryPersistence
        .findByUser(user)
        .filter { it.name in excludedCategories }
        .mapNotNull { it.id }
        .toSet()
  }

  private fun calculateCategoryExpenses(transactions: List<Transaction>): Map<UUID?, Double> {
    return transactions
        .groupBy { it.category?.id }
        .mapValues { it.value.sumOf { transaction -> transaction.personalShare } }
  }

  private fun filterCategoryLimits(
      categoryLimits: List<CategoryBudget>,
      user: User
  ): List<CategoryBudget> {
    val excludedCategories = setOf("Transfer")
    return categoryLimits.filter { categoryBudget ->
      val category = categoryPersistence.findByIdAndUser(categoryBudget.categoryId, user)
      category != null && category.name !in excludedCategories
    }
  }

  private fun mapToCategoryBudgetDetailsResponses(
      categoryLimits: List<CategoryBudget>,
      categoryExpenses: Map<UUID?, Double>,
      user: User
  ): List<CategoryBudgetDetailsResponse> {
    return categoryLimits.map { categoryBudget ->
      val spent = categoryExpenses[categoryBudget.categoryId] ?: 0.0
      CategoryBudgetDetailsResponse(
          categoryId = categoryBudget.categoryId,
          categoryName =
              categoryPersistence.findByIdAndUser(categoryBudget.categoryId, user)?.name
                  ?: throw RuntimeException("Category not found"),
          limit = categoryBudget.budgetAmount,
          spent = spent)
    }
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

  private fun mapToCategoryResponse(category: Category): CategoryResponse {
    return CategoryResponse(
        id = category.id!!,
        name = category.name,
        isActive = category.isActive,
        isEditable = category.isEditable)
  }
}
