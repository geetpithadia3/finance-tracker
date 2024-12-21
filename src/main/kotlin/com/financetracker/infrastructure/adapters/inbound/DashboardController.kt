package com.financetracker.infrastructure.adapters.inbound

import com.financetracker.application.ports.input.DashboardManagementUseCase
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.response.DashboardDetailsResponse
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.UserRepository
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.YearMonth

@RestController
class DashboardController(
    val dashboardManagementUseCase: DashboardManagementUseCase,
    private val userRepository: UserRepository
) {

  @GetMapping("/dashboard")
  fun listDetails(
      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") yearMonth: YearMonth?
  ): ResponseEntity<DashboardDetailsResponse> {
    val user = getCurrentUser()
    val targetYearMonth = yearMonth ?: YearMonth.now()
    return ResponseEntity.ok(dashboardManagementUseCase.getMonthDetails(targetYearMonth, user))
  }

  @GetMapping("/dashboard/expenses-by-category")
  fun getExpensesByCategory(
      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") yearMonth: YearMonth?
  ): ResponseEntity<Map<String, Double>> {
    val user = getCurrentUser()
    val targetYearMonth = yearMonth ?: YearMonth.now()
      return ResponseEntity.ok(dashboardManagementUseCase.getExpensesByCategory(targetYearMonth, user))
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
