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

@RestController
@RequestMapping("/goal")
class GoalController(
    private val goalManagementUseCase: GoalManagementUseCase,
    private val userRepository: UserRepository
) {

  @PostMapping
  fun createGoal(@RequestBody request: CreateGoalRequest): ResponseEntity<GoalResponse> {
    val user = getCurrentUser()
    return ResponseEntity.ok(goalManagementUseCase.createGoal(request, user))
  }

  @PutMapping("/progress")
  fun updateGoalProgress(
      @RequestBody request: UpdateGoalProgressRequest
  ): ResponseEntity<GoalResponse> {
    val user = getCurrentUser()
    return ResponseEntity.ok(goalManagementUseCase.updateGoalProgress(request, user))
  }

  @GetMapping
  fun listGoals(): ResponseEntity<List<GoalResponse>> {
    val user = getCurrentUser()
    return ResponseEntity.ok(goalManagementUseCase.listGoals(user))
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
