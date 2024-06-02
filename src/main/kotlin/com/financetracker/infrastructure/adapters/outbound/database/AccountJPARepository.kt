package com.financetracker.infrastructure.adapters.outbound.database

import com.financetracker.infrastructure.adapters.outbound.database.entities.AccountEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AccountJPARepository : JpaRepository<AccountEntity, String> {
  fun findById(id: UUID): Optional<AccountEntity>
}
