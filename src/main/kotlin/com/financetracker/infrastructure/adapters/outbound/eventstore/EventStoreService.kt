package com.financetracker.infrastructure.adapters.outbound.eventstore

import com.fasterxml.jackson.databind.ObjectMapper
import com.financetracker.application.spi.EventStore
import com.financetracker.domain.account.events.AccountCreated
import com.financetracker.domain.account.events.AccountCredited
import com.financetracker.domain.account.events.AccountDebited
import com.financetracker.domain.account.events.AccountEvent
import com.financetracker.infrastructure.adapters.outbound.eventstore.entities.EventEntity
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.*

@Service
class EventStoreService(private val eventStore: JPAEventStore, private val mapper: ObjectMapper) :
    EventStore {
  override fun save(event: AccountEvent) {
    val eventEntity =
        EventEntity(
            id = UUID.randomUUID(),
            entityId = event.id,
            type = event::class.java.simpleName,
            data = mapper.writeValueAsString(event),
            createdAt = Instant.now())
    eventStore.save(eventEntity)
  }

  override fun query(accountId: UUID): List<AccountEvent> {
    val events = eventStore.findAllByEntityId(accountId)
    return events.map {
      when (it.type) {
        "AccountCreated" -> mapper.readValue(it.data, AccountCreated::class.java)
        "AccountCredited" -> mapper.readValue(it.data, AccountCredited::class.java)
        "AccountDebited" -> mapper.readValue(it.data, AccountDebited::class.java)
        else -> {}
      }
    } as List<AccountEvent>
  }

  override fun queryBetween(
      accountId: UUID,
      startDate: LocalDate,
      endDate: LocalDate
  ): List<AccountEvent> {
    val events =
        eventStore.findAllByEntityIdAndCreatedAtBetween(
            accountId,
            startDate.atStartOfDay().toInstant(ZonedDateTime.now().offset),
            endDate.atTime(LocalTime.MAX).toInstant(ZonedDateTime.now().offset))
    return events.map {
      when (it.type) {
        "AccountCreated" -> mapper.readValue(it.data, AccountCreated::class.java)
        "AccountCredited" -> mapper.readValue(it.data, AccountCredited::class.java)
        "AccountDebited" -> mapper.readValue(it.data, AccountDebited::class.java)
        else -> {}
      }
    } as List<AccountEvent>
  }
}
