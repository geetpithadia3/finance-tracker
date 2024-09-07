package com.financetracker.infrastructure.adapters.outbound.persistence.repository

import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<UserEntity, UUID> {
  fun findByUsername(username: String): UserEntity?
}
