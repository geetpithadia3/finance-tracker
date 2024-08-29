package com.financetracker.application.ports.input

import com.financetracker.application.dto.request.CreateCategoryRequest
import com.financetracker.application.dto.response.CategoryResponse

interface CategoryManagementUseCase {

  fun create(request: CreateCategoryRequest): Long

  fun list(): List<CategoryResponse>
}
