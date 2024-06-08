package com.financetracker.infrastructure.adapters.outbound.persistence.respository

import com.financetracker.infrastructure.adapters.outbound.persistence.entity.AccountBalance
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AccountBalanceRepository : JpaRepository<AccountBalance, String> {
  fun findByAccountId(accountId: String): Optional<AccountBalance>
}
