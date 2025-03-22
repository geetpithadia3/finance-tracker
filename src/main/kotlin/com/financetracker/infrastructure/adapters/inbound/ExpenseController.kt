package com.financetracker.infrastructure.adapters.inbound

import com.financetracker.application.ports.input.ExpenseManagementUseCase
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.ListExpensesByMonthRequest
import com.financetracker.infrastructure.adapters.inbound.dto.response.ExpenseResponse
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ExpenseController(
    private val expenseManagementUseCase: ExpenseManagementUseCase,
    private val userRepository: UserRepository
): BaseController(userRepository) {
  private val logger = LoggerFactory.getLogger(ExpenseController::class.java)

  @PostMapping("/expenses")
  fun listExpensesByMonth(
      @RequestBody request: ListExpensesByMonthRequest
  ): ResponseEntity<List<ExpenseResponse>> {
    val user = getCurrentUser()
    logger.info(
        "Received request to list expenses for user: ${user.username}, month: ${request.yearMonth}")
    return try {
      val response = expenseManagementUseCase.list(request, user)
      logger.info(
          "Successfully fetched ${response.size} expenses for user: ${user.username}, month: ${request.yearMonth}")
      ResponseEntity.ok(response)
    } catch (e: Exception) {
      logger.error(
          "Error fetching expenses for user: ${user.username}, month: ${request.yearMonth}", e)
      throw e
    }
  }
}
