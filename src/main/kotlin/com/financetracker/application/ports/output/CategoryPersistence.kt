package com.financetracker.application.ports.output

import com.financetracker.domain.model.Category
import com.financetracker.domain.model.User
import java.util.*

interface CategoryPersistence {
  fun save(category: Category): UUID

  fun findByUser(user: User): List<Category>

  fun findByIdAndUser(id: UUID, user: User): Category?

  fun findByNameAndUser(name: String, user: User): Category?

  fun update(category: Category): Category

  fun findById(categoryId: UUID): Category?
}
