package com.financetracker.infrastructure.adapters.inbound

import com.financetracker.application.ports.input.IncomeSourceManagementUseCase
import com.financetracker.domain.model.IncomeSource
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.AddIncomeSourceRequest
import com.financetracker.infrastructure.adapters.inbound.dto.request.UpdateIncomeSourceRequest
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/income-sources")
class IncomeSourceController(
    private val incomeSourceManagementUseCase: IncomeSourceManagementUseCase,
    private val userRepository: UserRepository
) {
  private val logger = LoggerFactory.getLogger(IncomeSourceController::class.java)

  @PostMapping
  fun addIncomeSource(@RequestBody request: AddIncomeSourceRequest): ResponseEntity<IncomeSource> {
    val user = getCurrentUser()
    logger.info("Received request to add income source for user: ${user.username}")
    return try {
      val response = incomeSourceManagementUseCase.addIncomeSource(request, user)
      logger.info("Successfully added income source for user: ${user.username}")
      ResponseEntity.ok(response)
    } catch (e: Exception) {
      logger.error("Error adding income source for user: ${user.username}", e)
      throw e
    }
  }

  @PutMapping("/{id}")
  fun updateIncomeSource(
      @PathVariable id: UUID,
      @RequestBody request: UpdateIncomeSourceRequest
  ): ResponseEntity<IncomeSource> {
    val user = getCurrentUser()
    logger.info("Received request to update income source $id for user: ${user.username}")
    return try {
      val response = incomeSourceManagementUseCase.updateIncomeSource(id, request, user)
      logger.info("Successfully updated income source $id for user: ${user.username}")
      ResponseEntity.ok(response)
    } catch (e: Exception) {
      logger.error("Error updating income source $id for user: ${user.username}", e)
      throw e
    }
  }

  @GetMapping
  fun listIncomeSources(): ResponseEntity<List<IncomeSource>> {
    val user = getCurrentUser()
    logger.info("Received request to list income sources for user: ${user.username}")
    return try {
      val response = incomeSourceManagementUseCase.listIncomeSources(user)
      logger.info("Successfully fetched ${response.size} income sources for user: ${user.username}")
      ResponseEntity.ok(response)
    } catch (e: Exception) {
      logger.error("Error fetching income sources for user: ${user.username}", e)
      throw e
    }
  }

  @DeleteMapping("/{id}")
  fun removeIncomeSource(@PathVariable id: UUID): ResponseEntity<Unit> {
    val user = getCurrentUser()
    logger.info("Received request to remove income source $id for user: ${user.username}")
    return try {
      incomeSourceManagementUseCase.removeIncomeSource(id, user)
      logger.info("Successfully removed income source $id for user: ${user.username}")
      ResponseEntity.ok().build()
    } catch (e: Exception) {
      logger.error("Error removing income source $id for user: ${user.username}", e)
      throw e
    }
  }

  @GetMapping("/monthly-total")
  fun getMonthlyIncome(): ResponseEntity<Double> {
    val user = getCurrentUser()
    logger.info("Received request to calculate monthly income for user: ${user.username}")
    return try {
      val total = incomeSourceManagementUseCase.calculateMonthlyIncome(user)
      logger.info("Successfully calculated monthly income for user: ${user.username}")
      ResponseEntity.ok(total)
    } catch (e: Exception) {
      logger.error("Error calculating monthly income for user: ${user.username}", e)
      throw e
    }
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
