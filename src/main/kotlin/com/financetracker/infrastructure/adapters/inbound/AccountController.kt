package com.financetracker.infrastructure.adapters.inbound

import com.financetracker.application.ports.input.AccountManagementUseCase
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.CreateAccountRequest
import com.financetracker.infrastructure.adapters.inbound.dto.response.AccountBalanceResponse
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountController(
    val accountManagementUseCase: AccountManagementUseCase,
    val userRepository: UserRepository
) {

  @PostMapping("/account")
  fun create(@RequestBody request: CreateAccountRequest): ResponseEntity<AccountBalanceResponse> {
    val user = getCurrentUser()
    return ResponseEntity.ok(
        accountManagementUseCase.create(
            request, User(username = user.username, password = user.password)))
  }

  @GetMapping("/account")
  fun balances(): ResponseEntity<List<AccountBalanceResponse>> {
    val user = getCurrentUser()
    return ResponseEntity.ok(
        accountManagementUseCase.list(User(username = user.username, password = user.password)))
  }

  //  @DeleteMapping("/account")
  //  fun deleteAccount(@PathVariable account: String): ResponseEntity<Unit> {
  //    val user = getCurrentUser()
  //    return ResponseEntity.ok(accountApplicationService.deleteAccount(account, user))
  //  }

  //  @PostMapping("/transactions")
  //  fun addTransactions(@RequestBody request: List<AddTransactionRequest>): ResponseEntity<Unit> {
  //    val user = getCurrentUser()
  //    accountApplicationService.addTransactions(request, user)
  //    return ResponseEntity.ok().build()
  //  }
  private fun getCurrentUser(): UserEntity {
    val authentication = SecurityContextHolder.getContext().authentication
    val username = authentication.name
    return userRepository.findByUsername(username) ?: throw RuntimeException("User not found")
  }
}
