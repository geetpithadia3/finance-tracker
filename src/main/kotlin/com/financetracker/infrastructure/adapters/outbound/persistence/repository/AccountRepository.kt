package com.financetracker.infrastructure.adapters.outbound.persistence.repository

import com.financetracker.infrastructure.adapters.outbound.persistence.entity.AccountEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository : JpaRepository<AccountEntity, String> {
  fun findByUser(user: UserEntity): List<AccountEntity>

  fun findByIdAndUser(id: String, user: UserEntity): AccountEntity?
}
