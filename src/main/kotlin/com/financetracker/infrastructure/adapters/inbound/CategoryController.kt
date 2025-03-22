package com.financetracker.infrastructure.adapters.inbound

import com.financetracker.application.ports.input.CategoryManagementUseCase
import com.financetracker.infrastructure.adapters.inbound.dto.request.CreateCategoryRequest
import com.financetracker.infrastructure.adapters.inbound.dto.request.UpdateCategoryRequest
import com.financetracker.infrastructure.adapters.inbound.dto.response.CategoryResponse
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.UserRepository
import java.util.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/categories")
class CategoryController(
    private val categoryManagementUseCase: CategoryManagementUseCase,
    private val userRepository: UserRepository
) : BaseController(userRepository) {
  @GetMapping
  fun listCategories(): ResponseEntity<List<CategoryResponse>> {
    val user = getCurrentUser()
    return ResponseEntity.ok(categoryManagementUseCase.listAll(user))
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
    return ResponseEntity.ok(categoryManagementUseCase.update(id, request, user))
  }
}
