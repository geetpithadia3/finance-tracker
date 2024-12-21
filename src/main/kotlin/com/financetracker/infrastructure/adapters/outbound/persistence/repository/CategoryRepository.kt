package com.financetracker.infrastructure.adapters.outbound.persistence.repository

import com.financetracker.infrastructure.adapters.outbound.persistence.entity.CategoryEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CategoryRepository : JpaRepository<CategoryEntity, UUID> {
  fun findByUserAndIsActiveTrue(user: UserEntity): List<CategoryEntity>

  fun findByIdAndUser(id: UUID, user: UserEntity): CategoryEntity?

  fun findByNameAndUser(name: String, user: UserEntity): CategoryEntity?
}
