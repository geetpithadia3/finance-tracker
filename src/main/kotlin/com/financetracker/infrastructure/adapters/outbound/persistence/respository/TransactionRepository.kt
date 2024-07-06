package com.financetracker.infrastructure.adapters.outbound.persistence.respository

import com.financetracker.infrastructure.adapters.outbound.persistence.entity.TransactionView
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository interface TransactionRepository : JpaRepository<TransactionView, String> {}
