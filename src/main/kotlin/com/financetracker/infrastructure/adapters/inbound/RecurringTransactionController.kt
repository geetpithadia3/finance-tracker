package com.financetracker.infrastructure.adapters.inbound

import com.financetracker.application.ports.input.RecurringTransactionManagementUseCase
import com.financetracker.infrastructure.adapters.inbound.dto.request.UpdateRecurringTransactionStatusRequest
import com.financetracker.infrastructure.adapters.inbound.dto.response.RecurringTransactionResponse
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.UserRepository
import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/recurring-transactions")
class RecurringTransactionController(
    private val recurringTransactionManagementUseCase: RecurringTransactionManagementUseCase,
    userRepository: UserRepository
) : BaseController(userRepository) {

  private val logger = LoggerFactory.getLogger(AccountController::class.java)

  @GetMapping
  fun getAllActive(): ResponseEntity<List<RecurringTransactionResponse>> {
    val user = getCurrentUser()
    logger.info(
        "Received request to fetch all active recurring transactions for user: ${user.username}")
    val recurringTransactions = recurringTransactionManagementUseCase.findByAccountsAndActive(user)
    return ResponseEntity.ok(recurringTransactions)
  }

  @GetMapping("/{id}")
  fun getById(@PathVariable id: UUID): ResponseEntity<RecurringTransactionResponse> {
    val user = getCurrentUser()
    logger.info(
        "Received request to fetch recurring transaction by id: $id for user: ${user.username}")
    val recurringTransaction =
        recurringTransactionManagementUseCase.findById(id, user)
            ?: return ResponseEntity.notFound().build()

    return ResponseEntity.ok(recurringTransaction)
  }

  @DeleteMapping("/{id}")
  fun delete(@PathVariable id: UUID): ResponseEntity<Void> {
    val user = getCurrentUser()
    logger.info(
        "Received request to delete recurring transaction by id: $id for user: ${user.username}")
    recurringTransactionManagementUseCase.delete(id, user)
    return ResponseEntity.noContent().build()
  }

  @PutMapping("/{id}/status")
  fun updateStatus(
      @PathVariable id: UUID,
      @RequestBody request: UpdateRecurringTransactionStatusRequest
  ): ResponseEntity<RecurringTransactionResponse> {
    val user = getCurrentUser()
    logger.info(
        "Received request to update status of recurring transaction: $id to ${request.isActive} for user: ${user.username}")

    try {
      val updatedTransaction =
          recurringTransactionManagementUseCase.updateStatus(
              id = id, isActive = request.isActive, user = user)
      return ResponseEntity.ok(updatedTransaction)
    } catch (e: NoSuchElementException) {
      logger.error("Recurring transaction not found: $id", e)
      return ResponseEntity.notFound().build()
    } catch (e: SecurityException) {
      logger.error("User ${user.username} does not have access to recurring transaction: $id", e)
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
    } catch (e: Exception) {
      logger.error("Error updating recurring transaction status", e)
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
    }
  }
}
