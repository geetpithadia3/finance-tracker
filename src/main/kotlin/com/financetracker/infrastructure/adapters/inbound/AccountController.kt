package com.financetracker.infrastructure.adapters.inbound

import com.financetracker.application.ports.input.AccountManagementUseCase
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.CreateAccountRequest
import com.financetracker.infrastructure.adapters.inbound.dto.response.AccountBalanceResponse
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class AccountController(
    val accountManagementUseCase: AccountManagementUseCase,
    val userRepository: UserRepository
) {
  private val logger = LoggerFactory.getLogger(AccountController::class.java)

  @PostMapping("/account")
  fun create(@RequestBody request: CreateAccountRequest): ResponseEntity<AccountBalanceResponse> {
    logger.info("Received create account request for user: ${getCurrentUser().username}")
    return try {
      val user = getCurrentUser()
      val response = accountManagementUseCase.create(request, user)
      logger.info(
          "Account created successfully for user: ${user.username}, accountId: ${response.accountId}")
      ResponseEntity.ok(response)
    } catch (e: Exception) {
      logger.error("Error creating account for user: ${getCurrentUser().username}", e)
      throw e
    }
  }

  @GetMapping("/account")
  fun balances(): ResponseEntity<List<AccountBalanceResponse>> {
    logger.info("Received request to fetch account balances for user: ${getCurrentUser().username}")
    return try {
      val user = getCurrentUser()
      val response = accountManagementUseCase.list(user)
      logger.info(
          "Successfully fetched ${response.size} account balances for user: ${user.username}")
      ResponseEntity.ok(response)
    } catch (e: Exception) {
      logger.error("Error fetching account balances for user: ${getCurrentUser().username}", e)
      throw e
    }
  }

  @DeleteMapping("/account/{accountId}")
  fun deleteAccount(@PathVariable accountId: UUID): ResponseEntity<Unit> {
    logger.info(
        "Received request to delete account $accountId for user: ${getCurrentUser().username}")
    return try {
      val user = getCurrentUser()
      accountManagementUseCase.delete(accountId, user)
      logger.info("Successfully deleted account $accountId for user: ${user.username}")
      ResponseEntity.ok().build()
    } catch (e: Exception) {
      logger.error("Error deleting account $accountId for user: ${getCurrentUser().username}", e)
      throw e
    }
  }

  private fun getCurrentUser(): User {
    val authentication = SecurityContextHolder.getContext().authentication
    val username = authentication.name
    val userEntity =
        userRepository.findByUsername(username) ?: throw RuntimeException("User not found")
    return User(
        id = userEntity.id,
        username = userEntity.username,
        password = userEntity.password,
        externalId = userEntity.externalId,
        externalKey = userEntity.externalKey)
  }
}
