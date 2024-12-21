package com.financetracker.infrastructure.adapters.inbound

import com.financetracker.application.ports.input.CategoryManagementUseCase
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.CreateCategoryRequest
import com.financetracker.infrastructure.adapters.inbound.dto.request.UpdateCategoryRequest
import com.financetracker.infrastructure.adapters.inbound.dto.response.CategoryResponse
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/categories")
class CategoryController(
    private val categoryManagementUseCase: CategoryManagementUseCase,
    private val userRepository: UserRepository
) {
  @GetMapping
  fun listCategories(): ResponseEntity<List<CategoryResponse>> {
    val user = getCurrentUser()
    return ResponseEntity.ok(categoryManagementUseCase.list(user))
  }

  @PostMapping
  fun createCategory(
      @RequestBody request: CreateCategoryRequest
  ): ResponseEntity<CategoryResponse> {
    val user = getCurrentUser()
    return ResponseEntity.ok(categoryManagementUseCase.create(request, user))
  }

  @PutMapping("/{id}")
  fun updateCategory(
      @PathVariable id: UUID,
      @RequestBody request: UpdateCategoryRequest
  ): ResponseEntity<CategoryResponse> {
    val user = getCurrentUser()
    return ResponseEntity.ok(categoryManagementUseCase.update(request, user))
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
