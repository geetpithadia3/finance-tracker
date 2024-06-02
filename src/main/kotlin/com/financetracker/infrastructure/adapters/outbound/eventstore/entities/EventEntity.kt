package com.financetracker.infrastructure.adapters.outbound.eventstore.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.Instant
import java.util.*

@Entity(name = "event")
class EventEntity(
    @Id val id: UUID,
    @Column(name = "entity_id") val entityId: UUID,
    @Column(name = "type") val type: String,
    @Column(name = "data") val data: String,
    @Column(name = "created_at") val createdAt: Instant
)
