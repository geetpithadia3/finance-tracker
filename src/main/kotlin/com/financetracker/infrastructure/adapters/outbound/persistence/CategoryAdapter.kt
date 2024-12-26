package com.financetracker.infrastructure.adapters.outbound.persistence

import com.financetracker.application.ports.output.CategoryPersistence
import com.financetracker.domain.model.Category
import com.financetracker.domain.model.User
import com.financetracker.domain.model.toEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.toModel
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.CategoryRepository
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class CategoryAdapter(private val categoryRepository: CategoryRepository) : CategoryPersistence {

  override fun save(category: Category): UUID {
    val entity = category.toEntity()
    return categoryRepository.save(entity).id
  }

  override fun findByUser(user: User): List<Category> {
    val userEntity = UserEntity().apply { id = user.id!! }
    return categoryRepository.findByUserAndIsActiveTrue(userEntity).map { it.toModel() }
  }

  override fun findByIdAndUser(id: UUID, user: User): Category? {
    val userEntity = UserEntity().apply { this.id = user.id!! }
    return categoryRepository.findByIdAndUser(id, userEntity)?.toModel()
  }

  override fun findByNameAndUser(name: String, user: User): Category? {
    val userEntity = UserEntity().apply { id = user.id!! }
    return categoryRepository.findByNameAndUser(name, userEntity)?.toModel()
  }

  override fun update(category: Category): Category {
    val existingEntity =
        categoryRepository.findById(category.id!!).orElseThrow {
          NoSuchElementException("Category not found: ${category.id}")
        }

    existingEntity.apply {
      name = category.name
      isActive = category.isActive
      isEditable = category.isEditable
    }

    return categoryRepository.save(existingEntity).toModel()
  }

  override fun findById(categoryId: UUID): Category? {
    return categoryRepository.findById(categoryId).getOrNull()?.toModel()
  }
}
