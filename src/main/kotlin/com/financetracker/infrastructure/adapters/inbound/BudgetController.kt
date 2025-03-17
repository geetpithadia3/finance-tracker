package com.financetracker.infrastructure.adapters.inbound

import com.financetracker.application.ReportFormat
import com.financetracker.application.ports.input.BudgetManagementUseCase
import com.financetracker.infrastructure.adapters.inbound.dto.request.CreateBudgetRequest
import com.financetracker.infrastructure.adapters.inbound.dto.response.BudgetDetailsResponse
import com.financetracker.infrastructure.adapters.inbound.dto.response.BudgetResponse
import com.financetracker.infrastructure.adapters.inbound.dto.response.CategoryResponse
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.UserRepository
import java.time.YearMonth
import java.util.*
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/budgets")
class BudgetController(
    private val budgetManagementUseCase: BudgetManagementUseCase,
    private val userRepository: UserRepository
) : BaseController(userRepository) {

  @PostMapping
  fun createBudget(@RequestBody request: CreateBudgetRequest): ResponseEntity<BudgetResponse> {
    val user = getCurrentUser()
    return ResponseEntity.ok(budgetManagementUseCase.createBudget(request, user))
  }

  @GetMapping
  fun getBudgetDetails(
      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") yearMonth: YearMonth?
  ): ResponseEntity<BudgetDetailsResponse> {
    val user = getCurrentUser()
    val targetYearMonth = yearMonth ?: YearMonth.now()
    return ResponseEntity.ok(budgetManagementUseCase.getBudgetDetails(targetYearMonth, user))
  }

  @GetMapping("/categories")
  fun getBudgetableCategories(): ResponseEntity<List<CategoryResponse>> {
    val user = getCurrentUser()
    val categories = budgetManagementUseCase.getBudgetableCategories(user)
    return ResponseEntity.ok(categories)
  }

  @GetMapping("/report")
  fun downloadMonthlyReport(
      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") yearMonth: YearMonth?,
      @RequestParam(defaultValue = "PDF") format: ReportFormat
  ): ResponseEntity<ByteArray> {
    val user = getCurrentUser()
    val targetYearMonth = yearMonth ?: YearMonth.now()
    val reportContent = budgetManagementUseCase.generateMonthlyReport(targetYearMonth, user, format)
    
    val fileExtension = when (format) {
      ReportFormat.PDF -> "pdf"
      ReportFormat.CSV -> "csv"
    }
    
    val contentType = when (format) {
      ReportFormat.PDF -> "application/pdf"
      ReportFormat.CSV -> "text/csv"
    }
    
    return ResponseEntity.ok()
        .header(
            "Content-Disposition",
            "attachment; filename=budget-report-${targetYearMonth}.${fileExtension}")
        .header("Content-Type", contentType)
        .body(reportContent)
  }
}
