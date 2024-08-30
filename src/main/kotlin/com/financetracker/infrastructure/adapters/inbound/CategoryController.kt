package com.financetracker.infrastructure.adapters.inbound

import com.financetracker.application.ports.input.CategoryManagementUseCase
import com.financetracker.infrastructure.adapters.inbound.dto.response.CategoryResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CategoryController(
    val categoryManagementUseCase: CategoryManagementUseCase,
) {

  @GetMapping("/categories")
  fun listCategories(): ResponseEntity<List<CategoryResponse>> {
    return ResponseEntity.ok(categoryManagementUseCase.list())
  }
}
