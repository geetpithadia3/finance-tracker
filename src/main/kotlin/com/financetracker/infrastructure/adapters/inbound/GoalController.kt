package com.financetracker.infrastructure.adapters.inbound

import com.financetracker.application.ports.input.GoalManagementUseCase
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.CreateGoalRequest
import com.financetracker.infrastructure.adapters.inbound.dto.request.UpdateGoalProgressRequest
import com.financetracker.infrastructure.adapters.inbound.dto.response.GoalResponse
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.slf4j.LoggerFactory

@RestController
@RequestMapping("/goal")
class GoalController(
    private val goalManagementUseCase: GoalManagementUseCase,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(GoalController::class.java)

    @PostMapping
    fun createGoal(@RequestBody request: CreateGoalRequest): ResponseEntity<GoalResponse> {
        val user = getCurrentUser()
        logger.info("Received create goal request for user: ${user.username}")
        return try {
            val response = goalManagementUseCase.createGoal(request, user)
            logger.info("Goal created successfully for user: ${user.username}, goalId: ${response.id}")
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            logger.error("Error creating goal for user: ${user.username}", e)
            throw e
        }
    }

    @PutMapping("/progress")
    fun updateGoalProgress(
        @RequestBody request: UpdateGoalProgressRequest
    ): ResponseEntity<GoalResponse> {
        val user = getCurrentUser()
        logger.info("Received update goal progress request for user: ${user.username}, goalId: ${request.goalId}")
        return try {
            val response = goalManagementUseCase.updateGoalProgress(request, user)
            logger.info("Goal progress updated successfully for user: ${user.username}, goalId: ${response.id}")
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            logger.error("Error updating goal progress for user: ${user.username}, goalId: ${request.goalId}", e)
            throw e
        }
    }

    @GetMapping
    fun listGoals(): ResponseEntity<List<GoalResponse>> {
        val user = getCurrentUser()
        logger.info("Received request to list goals for user: ${user.username}")
        return try {
            val response = goalManagementUseCase.listGoals(user)
            logger.info("Successfully fetched ${response.size} goals for user: ${user.username}")
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            logger.error("Error fetching goals for user: ${user.username}", e)
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
