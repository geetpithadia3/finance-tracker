package com.financetracker.application

import com.financetracker.application.dto.response.CategoryResponse
import com.financetracker.application.ports.input.CategoryManagementUseCase
import com.financetracker.domain.account.model.Category
import org.springframework.stereotype.Service

@Service
class CategoryService() : CategoryManagementUseCase {

  override fun list(): List<CategoryResponse> {
    return Category.entries.map { CategoryResponse(name = it.name) }
  }
}
