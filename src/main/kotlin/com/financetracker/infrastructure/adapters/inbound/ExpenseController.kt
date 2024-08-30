package com.financetracker.infrastructure.adapters.inbound

import com.financetracker.application.ports.input.ExpenseManagementUseCase
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.RestController

@RestController
class ExpenseController(
    private val expenseManagementUseCase: ExpenseManagementUseCase,
    private val userRepository: UserRepository
) {

  //  @PostMapping("/transactions")
  //  fun transactions(
  //      @RequestBody request: TransactionsForMonthQuery
  //  ): ResponseEntity<List<MonthTransactionsView>> {
  //    val user = getCurrentUser()
  //    return ResponseEntity.ok(expenseManagementUseCase.getExpenses(request, user))
  //  }
  //
  //  @PostMapping("/transactions/update")
  //  fun update(@RequestBody request: List<UpdateTransactionRequest>): ResponseEntity<Unit> {
  //    val user = getCurrentUser()
  //    return ResponseEntity.ok(expenseManagementUseCase.updateExpenses(request, user))
  //  }

  private fun getCurrentUser(): UserEntity {
    val authentication = SecurityContextHolder.getContext().authentication
    val username = authentication.name
    return userRepository.findByUsername(username) ?: throw RuntimeException("User not found")
  }
}
