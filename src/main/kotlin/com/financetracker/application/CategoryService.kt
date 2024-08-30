package com.financetracker.application

import com.financetracker.application.ports.input.CategoryManagementUseCase
import com.financetracker.domain.model.Category
import com.financetracker.infrastructure.adapters.inbound.dto.response.CategoryResponse
import org.springframework.stereotype.Service

@Service
class CategoryService() : CategoryManagementUseCase {

  override fun list(): List<CategoryResponse> {
    return Category.entries.map { CategoryResponse(name = it.name) }
  }
}
