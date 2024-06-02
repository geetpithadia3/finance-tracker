package com.financetracker.infrastructure.adapters.outbound.eventstore

import com.financetracker.infrastructure.adapters.outbound.eventstore.entities.EventEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*

@Repository
interface JPAEventStore : JpaRepository<EventEntity, String> {

  fun findAllByEntityId(entityId: UUID): List<EventEntity>

  fun findAllByEntityIdAndCreatedAtBetween(
      entityId: UUID,
      startDate: Instant,
      endDate: Instant
  ): List<EventEntity>
}
