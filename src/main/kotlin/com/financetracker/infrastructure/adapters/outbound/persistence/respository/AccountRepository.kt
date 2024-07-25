package com.financetracker.infrastructure.adapters.outbound.persistence.respository

import com.financetracker.infrastructure.adapters.outbound.persistence.entity.account.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository interface AccountRepository : JpaRepository<Account, String> {}
