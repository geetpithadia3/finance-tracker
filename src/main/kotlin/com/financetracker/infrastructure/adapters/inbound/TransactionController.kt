package com.financetracker.infrastructure.adapters.inbound

import com.financetracker.application.dto.request.AddTransactionRequest
import com.financetracker.application.ports.input.TransactionManagementUseCase
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.outbound.persistence.respository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TransactionController(
    val transactionManagementUseCase: TransactionManagementUseCase,
    val userRepository: UserRepository
) {

  @PostMapping("/transactions")
  fun addTransactions(@RequestBody request: List<AddTransactionRequest>): ResponseEntity<Unit> {
    val user = getCurrentUser()
    transactionManagementUseCase.add(request, user)
    return ResponseEntity.ok().build()
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
