package com.financetracker.infrastructure.adapters.inbound

import com.financetracker.application.ports.input.TransactionManagementUseCase
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.AddTransactionRequest
import com.financetracker.infrastructure.adapters.inbound.dto.request.ListTransactionsByMonthRequest
import com.financetracker.infrastructure.adapters.inbound.dto.request.SyncAccountRequest
import com.financetracker.infrastructure.adapters.inbound.dto.request.UpdateTransactionRequest
import com.financetracker.infrastructure.adapters.inbound.dto.response.TransactionResponse
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TransactionController(
    val transactionManagementUseCase: TransactionManagementUseCase,
    val userRepository: UserRepository
) {
  private val logger = LoggerFactory.getLogger(TransactionController::class.java)

  @PostMapping("/transactions")
  fun addTransactions(@RequestBody request: List<AddTransactionRequest>): ResponseEntity<Unit> {
    val user = getCurrentUser()
    logger.info("Received request to add ${request.size} transactions for user: ${user.username}")
    return try {
      transactionManagementUseCase.add(request, user)
      logger.info("Successfully added ${request.size} transactions for user: ${user.username}")
      ResponseEntity.ok().build()
    } catch (e: Exception) {
      logger.error("Error adding transactions for user: ${user.username}", e)
      throw e
    }
  }

  @PutMapping("/transactions")
  fun updateTransactions(
      @RequestBody request: List<UpdateTransactionRequest>
  ): ResponseEntity<Unit> {
    val user = getCurrentUser()
    logger.info(
        "Received request to update ${request.size} transactions for user: ${user.username}")
    return try {
      transactionManagementUseCase.update(request, user)
      logger.info("Successfully updated ${request.size} transactions for user: ${user.username}")
      ResponseEntity.ok().build()
    } catch (e: Exception) {
      logger.error("Error updating transactions for user: ${user.username}", e)
      throw e
    }
  }

  @PostMapping("/transactions/list")
  fun listTransactions(
      @RequestBody request: ListTransactionsByMonthRequest
  ): ResponseEntity<List<TransactionResponse>> {
    val user = getCurrentUser()
    logger.info(
        "Received request to list transactions for user: ${user.username}, month: ${request.yearMonth}")
    return try {
      val response = transactionManagementUseCase.list(request, user)
      logger.info(
          "Successfully fetched ${response.size} expenses for user: ${user.username}, month: ${request.yearMonth}")
      ResponseEntity.ok(response)
    } catch (e: Exception) {
      logger.error(
          "Error fetching expenses for user: ${user.username}, month: ${request.yearMonth}", e)
      throw e
    }
  }

  @PostMapping("/sync-transactions")
  fun syncTransactions(@RequestBody request: SyncAccountRequest): ResponseEntity<Unit> {
    val user = getCurrentUser()
    logger.info("Received request to sync transactions for user: ${user.username}")
    return try {
      transactionManagementUseCase.syncWithSplitwise(request, user)
      logger.info("Successfully synced transactions for user: ${user.username}")
      ResponseEntity.ok().build()
    } catch (e: Exception) {
      logger.error("Error syncing transactions for user: ${user.username}", e)
      throw e
    }
  }

  //  @PutMapping("/transactions/share")
  //  fun shareTransaction(@RequestBody request: UpdateTransactionSharesRequest):
  // ResponseEntity<Unit> {
  //    val user = getCurrentUser()
  //    logger.info(
  //        "Received request to update shares for transaction ${request.id} for user:
  // ${user.username}")
  //    return try {
  //      transactionManagementUseCase.updateWithShares(request, user)
  //      logger.info(
  //          "Successfully updated shares for transaction ${request.id} user: ${user.username}")
  //      ResponseEntity.ok().build()
  //    } catch (e: Exception) {
  //      logger.error("Error updating transactions for user: ${user.username}", e)
  //      throw e
  //    }
  //  }

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
