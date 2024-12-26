package com.financetracker.application.ports.input

import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.CreateCategoryRequest
import com.financetracker.infrastructure.adapters.inbound.dto.request.UpdateCategoryRequest
import com.financetracker.infrastructure.adapters.inbound.dto.response.CategoryResponse
import java.util.*

interface CategoryManagementUseCase {

  fun create(request: CreateCategoryRequest, user: User): CategoryResponse

  fun listEnabled(user: User): List<CategoryResponse>

  fun listAll(user: User): List<CategoryResponse>

  fun update(id: UUID, request: UpdateCategoryRequest, user: User): CategoryResponse
}
