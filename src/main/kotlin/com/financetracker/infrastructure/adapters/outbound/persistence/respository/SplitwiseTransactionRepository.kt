package com.financetracker.infrastructure.adapters.outbound.persistence.respository

import com.financetracker.infrastructure.adapters.outbound.persistence.entity.SplitwiseTransaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository interface SplitwiseTransactionRepository : JpaRepository<SplitwiseTransaction, Long> {}
