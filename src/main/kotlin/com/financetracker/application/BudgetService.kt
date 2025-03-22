package com.financetracker.application

import com.financetracker.application.ports.input.BudgetManagementUseCase
import com.financetracker.application.ports.output.AccountPersistence
import com.financetracker.application.ports.output.BudgetPersistence
import com.financetracker.application.ports.output.CategoryPersistence
import com.financetracker.application.ports.output.TransactionPersistence
import com.financetracker.domain.model.*
import com.financetracker.infrastructure.adapters.inbound.dto.request.CategoryBudgetRequest
import com.financetracker.infrastructure.adapters.inbound.dto.request.CreateBudgetRequest
import com.financetracker.infrastructure.adapters.inbound.dto.response.*
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import java.io.ByteArrayOutputStream
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
    val excludedCategories = setOf(
        CategoryName.TRANSFER.name, 
        CategoryName.INCOME.name,
        CategoryName.CREDIT_CARD_PAYMENT.name
    )
    return categoryPersistence
        .findByUser(user)
        .filter { it.isActive && !excludedCategories.contains(it.name) }
        .map { mapToCategoryResponse(it) }
  }

  override fun generateMonthlyReport(
      yearMonth: YearMonth,
      user: User,
      format: ReportFormat
  ): ByteArray {
    val budgetDetails = getBudgetDetails(yearMonth, user)
    val accounts = accountPersistence.findByUser(user)
    val startDate = yearMonth.atDay(1)
    val endDate = yearMonth.atEndOfMonth()
    val transactions =
        transactionPersistence.findByAccountInAndOccurredOnBetween(accounts, startDate, endDate)
    val transactionsByCategory = transactions.groupBy { it.category?.name }

    return when (format) {
      ReportFormat.PDF -> generatePdfReport(yearMonth, budgetDetails, transactionsByCategory)
      ReportFormat.CSV -> generateCsvReport(yearMonth, budgetDetails, transactionsByCategory)
    }
  }

  private fun generatePdfReport(
      yearMonth: YearMonth,
      budgetDetails: BudgetDetailsResponse,
      transactionsByCategory: Map<String?, List<Transaction>>
  ): ByteArray {
    val outputStream = ByteArrayOutputStream()

    val pdfWriter = PdfWriter(outputStream)
    val pdf = PdfDocument(pdfWriter)
    val document = Document(pdf)

    document.add(
        Paragraph("Monthly Financial Report - ${yearMonth.month} ${yearMonth.year}")
            .setFontSize(20f)
            .setBold())

    // Add Summary Section
    val totalBudget = budgetDetails.categories.sumOf { it.limit }
    val totalSpent = budgetDetails.categories.sumOf { it.spent }
    document.add(Paragraph("Budget Summary").setFontSize(16f).setBold())
    document.add(Paragraph("Total Budget: $${String.format("%.2f", totalBudget)}"))
    document.add(Paragraph("Total Spent: $${String.format("%.2f", totalSpent)}"))
    document.add(Paragraph("Remaining: $${String.format("%.2f", totalBudget - totalSpent)}"))

    // Add Category Breakdown
    document.add(Paragraph("Category Breakdown").setFontSize(16f).setBold())

    budgetDetails.categories.forEach { category ->
      document.add(Paragraph(category.categoryName).setFontSize(14f).setBold())
      document.add(Paragraph("Budget: $${String.format("%.2f", category.limit)}"))
      document.add(Paragraph("Spent: $${String.format("%.2f", category.spent)}"))
      document.add(
          Paragraph("Remaining: $${String.format("%.2f", category.limit - category.spent)}"))

      // Add transactions table for this category
      val categoryTransactions = transactionsByCategory[category.categoryName] ?: emptyList()
      if (categoryTransactions.isNotEmpty()) {
        val table = Table(3).useAllAvailableWidth()
        table.addCell("Date")
        table.addCell("Description")
        table.addCell("Amount")

        categoryTransactions
            .sortedBy { it.occurredOn }
            .forEach { transaction ->
              table.addCell(transaction.occurredOn.toString())
              table.addCell(transaction.description)
              table.addCell("$${String.format("%.2f", transaction.amount)}")
            }
        document.add(table)
      } else {
        document.add(Paragraph("No transactions for this category"))
      }
      document.add(Paragraph("\n"))
    }

    document.close()
    return outputStream.toByteArray()
  }

  private fun generateCsvReport(
      yearMonth: YearMonth,
      budgetDetails: BudgetDetailsResponse,
      transactionsByCategory: Map<String?, List<Transaction>>
  ): ByteArray {
    val csvContent = StringBuilder()
    
    // Helper function to escape CSV fields
    fun escapeCsv(field: String?): String {
        if (field == null) return ""
        return if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            "\"${field.replace("\"", "\"\"")}\""
        } else {
            field
        }
    }
    
    // Helper function to format currency
    fun formatCurrency(amount: Double): String {
        return "$${String.format("%.2f", amount)}"
    }
    
    // Write header with BOM for Excel compatibility
    csvContent.append('\ufeff') // Add BOM
    csvContent.appendLine("Monthly Financial Report - ${yearMonth.month} ${yearMonth.year}")
    csvContent.appendLine()
    
    // Write Summary
    val totalBudget = budgetDetails.categories.sumOf { it.limit }
    val totalSpent = budgetDetails.categories.sumOf { it.spent }
    csvContent.appendLine("Budget Summary")
    csvContent.appendLine("Category,Amount")
    csvContent.appendLine("Total Budget,${formatCurrency(totalBudget)}")
    csvContent.appendLine("Total Spent,${formatCurrency(totalSpent)}")
    csvContent.appendLine("Remaining,${formatCurrency(totalBudget - totalSpent)}")
    csvContent.appendLine()
    
    // Write Category Details
    csvContent.appendLine("Category Breakdown")
    budgetDetails.categories.forEach { category ->
        csvContent.appendLine()
        csvContent.appendLine("Category: ${escapeCsv(category.categoryName)}")
        csvContent.appendLine("Type,Amount")
        csvContent.appendLine("Budget,${formatCurrency(category.limit)}")
        csvContent.appendLine("Spent,${formatCurrency(category.spent)}")
        csvContent.appendLine("Remaining,${formatCurrency(category.limit - category.spent)}")
        
        // Write transactions
        val categoryTransactions = transactionsByCategory[category.categoryName] ?: emptyList()
        if (categoryTransactions.isNotEmpty()) {
            csvContent.appendLine()
            csvContent.appendLine("Transactions")
            csvContent.appendLine("Date,Description,Amount")
            categoryTransactions
                .sortedBy { it.occurredOn }
                .forEach { transaction ->
                    csvContent.appendLine(
                        listOf(
                            transaction.occurredOn?.toString() ?: "",
                            escapeCsv(transaction.description),
                            formatCurrency(transaction.amount)
                        ).joinToString(",")
                    )
                }
        } else {
            csvContent.appendLine("No transactions for this category")
        }
        csvContent.appendLine()
    }
    
    return csvContent.toString().toByteArray(Charsets.UTF_8)
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

    if (category.name in setOf(
        CategoryName.TRANSFER.name, 
        CategoryName.INCOME.name,
        CategoryName.CREDIT_CARD_PAYMENT.name
    )) {
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

  private fun createNewBudget(
      yearMonth: YearMonth,
      categoryLimits: List<CategoryBudget>,
      user: User
  ): BudgetResponse {
    val budget = Budget(userId = user.id!!, yearMonth = yearMonth, categoryLimits = categoryLimits)
    val savedBudget = budgetPersistence.save(budget)
    return mapToBudgetResponse(savedBudget, user)
  }

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

enum class ReportFormat {
  PDF,
  CSV
}
