package com.financetracker.application.ports.output

import com.financetracker.domain.model.Category

interface CategoryPersistence {
  fun save(category: Category): Long

  fun list(): List<Category>
}
