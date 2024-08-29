package com.financetracker.application

import com.financetracker.application.dto.request.CreateCategoryRequest
import com.financetracker.application.dto.response.CategoryResponse
import com.financetracker.application.ports.input.CategoryManagementUseCase
import com.financetracker.application.ports.output.CategoryPersistence
import com.financetracker.domain.model.Category
import org.springframework.stereotype.Service

@Service
class CategoryService(private val categoryPersistence: CategoryPersistence) :
    CategoryManagementUseCase {
  override fun create(request: CreateCategoryRequest): Long {
    return categoryPersistence.save(Category(request.name))
  }

  override fun list(): List<CategoryResponse> {
    return categoryPersistence.list().map { CategoryResponse(name = it.name) }
  }
}
