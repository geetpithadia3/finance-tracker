package com.financetracker.infrastructure.adapters.inbound

import com.financetracker.application.ports.input.BudgetManagementUseCase
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.CreateBudgetRequest
import com.financetracker.infrastructure.adapters.inbound.dto.request.UpdateBudgetRequest
import com.financetracker.infrastructure.adapters.inbound.dto.response.BudgetDetailsResponse
import com.financetracker.infrastructure.adapters.inbound.dto.response.BudgetResponse
import com.financetracker.infrastructure.adapters.inbound.dto.response.CategoryResponse
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.UserRepository
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.time.YearMonth
import java.util.*

@RestController
@RequestMapping("/budgets")
class BudgetController(
    private val budgetManagementUseCase: BudgetManagementUseCase,
    private val userRepository: UserRepository
) {

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

  @PutMapping("/{id}")
  fun updateBudget(
      @PathVariable id: UUID,
      @RequestBody request: UpdateBudgetRequest
  ): ResponseEntity<BudgetResponse> {
    val user = getCurrentUser()
    return ResponseEntity.ok(budgetManagementUseCase.updateBudget(id, request, user))
  }

  @GetMapping("/categories")
  fun getBudgetableCategories(): ResponseEntity<List<CategoryResponse>> {
    val user = getCurrentUser()
    val categories = budgetManagementUseCase.getBudgetableCategories(user)
    return ResponseEntity.ok(categories)
  }

  private fun getCurrentUser(): User {
    val authentication = SecurityContextHolder.getContext().authentication
    val username = authentication.name
    val entity = userRepository.findByUsername(username) ?: throw RuntimeException("User not found")
    return User(
        id = entity.id,
        username = entity.username,
        password = entity.password,
        externalId = entity.externalId,
        externalKey = entity.externalKey)
  }
}
