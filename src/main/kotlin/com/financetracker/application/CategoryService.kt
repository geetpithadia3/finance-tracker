package com.financetracker.application

import com.financetracker.application.ports.input.CategoryManagementUseCase
import com.financetracker.domain.model.Category
import org.springframework.stereotype.Service

@Service
class CategoryService() : CategoryManagementUseCase {

  override fun list(): List<String> {
    return Category.entries.map { it.name }
  }
}
