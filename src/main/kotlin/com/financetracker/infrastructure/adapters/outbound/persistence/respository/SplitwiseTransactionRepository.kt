package com.financetracker.infrastructure.adapters.outbound.persistence.respository

import com.financetracker.infrastructure.adapters.outbound.persistence.entity.SplitwiseTransaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface SplitwiseTransactionRepository : JpaRepository<SplitwiseTransaction, Long> {

  @Query(
      "select updated_on from public.splitwise_transaction order by updated_on limit 1",
      nativeQuery = true)
  fun findLastUpdatedDate(): LocalDate?
}
