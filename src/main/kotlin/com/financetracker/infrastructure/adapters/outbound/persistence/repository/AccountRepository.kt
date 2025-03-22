package com.financetracker.infrastructure.adapters.outbound.persistence.repository

import com.financetracker.infrastructure.adapters.outbound.persistence.entity.AccountEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AccountRepository : JpaRepository<AccountEntity, UUID> {
  fun findByUser(user: UserEntity): List<AccountEntity>

  fun findByIdAndUser(id: UUID, user: UserEntity): AccountEntity?

  fun deleteByIdAndUser(id: UUID, user: UserEntity)
}
