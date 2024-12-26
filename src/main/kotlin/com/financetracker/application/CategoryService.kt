package com.financetracker.application

import com.financetracker.application.ports.input.CategoryManagementUseCase
import com.financetracker.application.ports.output.CategoryPersistence
import com.financetracker.domain.model.Category
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.CreateCategoryRequest
import com.financetracker.infrastructure.adapters.inbound.dto.request.UpdateCategoryRequest
import com.financetracker.infrastructure.adapters.inbound.dto.response.CategoryResponse
import org.springframework.stereotype.Service
import java.util.*

@Service
class CategoryService(private val categoryPersistence: CategoryPersistence) :
    CategoryManagementUseCase {

  override fun create(request: CreateCategoryRequest, user: User): CategoryResponse {
    val existingCategory = categoryPersistence.findByNameAndUser(request.name, user)
    if (existingCategory != null) {
      throw IllegalArgumentException("Category with name ${request.name} already exists")
    }

    val category =
        Category(name = request.name, isEditable = request.isEditable, userId = user.id!!)

    val id = categoryPersistence.save(category)
    return CategoryResponse(id, category.name, category.isActive, category.isEditable)
  }

  override fun listEnabled(user: User): List<CategoryResponse> {
    return categoryPersistence.findByUser(user).map {
      CategoryResponse(it.id!!, it.name, it.isActive, it.isEditable)
    }
  }

  override fun listAll(user: User): List<CategoryResponse> {
    return categoryPersistence.findByUser(user).map {
      CategoryResponse(it.id!!, it.name, it.isActive, it.isEditable)
    }
  }

  override fun update(id: UUID, request: UpdateCategoryRequest, user: User): CategoryResponse {
    val category =
        categoryPersistence.findByIdAndUser(id, user)
            ?: throw NoSuchElementException("Category not found")

    if (!category.isEditable) {
      throw IllegalStateException("Category ${category.name} is not editable")
    }

    val updatedCategory =
        category.copy(
            name = request.name, isActive = request.isActive, isEditable = request.isEditable)

    val saved = categoryPersistence.update(updatedCategory)
    return CategoryResponse(saved.id!!, saved.name, saved.isActive, saved.isEditable)
  }
}
