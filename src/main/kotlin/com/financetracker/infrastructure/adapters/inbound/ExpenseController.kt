package com.financetracker.infrastructure.adapters.inbound

import com.financetracker.application.ExpensesApplicationService
import com.financetracker.application.queries.TransactionsForMonthQuery
import com.financetracker.domain.account.projections.MonthTransactionsView
import com.financetracker.infrastructure.adapters.inbound.dto.UpdateTransactionRequest
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.respository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ExpenseController(
    private val expensesApplicationService: ExpensesApplicationService,
    private val userRepository: UserRepository
) {

  @PostMapping("/transactions")
  fun transactions(
      @RequestBody request: TransactionsForMonthQuery
  ): ResponseEntity<List<MonthTransactionsView>> {
    val user = getCurrentUser()
    return ResponseEntity.ok(expensesApplicationService.getExpenses(request, user))
  }

  @PostMapping("/transactions/update")
  fun update(@RequestBody request: List<UpdateTransactionRequest>): ResponseEntity<Unit> {
    val user = getCurrentUser()
    return ResponseEntity.ok(expensesApplicationService.updateExpenses(request, user))
  }

  private fun getCurrentUser(): UserEntity {
    val authentication = SecurityContextHolder.getContext().authentication
    val username = authentication.name
    return userRepository.findByUsername(username) ?: throw RuntimeException("User not found")
  }
}
