package com.financetracker.infrastructure.adapters.outbound.persistence

import com.financetracker.application.ports.output.CategoryPersistence
import com.financetracker.domain.model.Category
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.CategoryEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.respository.CategoryRepository
import org.springframework.stereotype.Service

@Service
class CategoryAdapter(val categoryRepository: CategoryRepository) : CategoryPersistence {
  override fun save(category: Category): Long {
    return categoryRepository.save(CategoryEntity().apply { name = category.name }).id!!
  }

  override fun list(): List<Category> {
    return categoryRepository.findAll().map { Category(name = it.name) }
  }
}
