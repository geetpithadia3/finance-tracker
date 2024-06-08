package com.financetracker.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import org.axonframework.common.jpa.EntityManagerProvider
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition
import org.axonframework.eventsourcing.SnapshotTriggerDefinition
import org.axonframework.eventsourcing.Snapshotter
import org.axonframework.eventsourcing.eventstore.EventStorageEngine
import org.axonframework.eventsourcing.eventstore.jpa.JpaEventStorageEngine
import org.axonframework.serialization.Serializer
import org.axonframework.spring.messaging.unitofwork.SpringTransactionManager
import org.springframework.context.annotation.Bean
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.stereotype.Component

@Component
class SpringBeans {

  fun jacksonObjectMapper(): ObjectMapper {
    val mapper = jacksonObjectMapper()
    mapper.registerModule(JavaTimeModule())
    return mapper
  }

  @Bean
  fun storageEngine(
      serializer: Serializer,
      entityManagerProvider: EntityManagerProvider,
      jpaTransactionManager: JpaTransactionManager
  ): EventStorageEngine {
    return JpaEventStorageEngine.builder()
        .eventSerializer(serializer)
        .entityManagerProvider(entityManagerProvider)
        .transactionManager(SpringTransactionManager(jpaTransactionManager))
        .build()
  }

  @Bean
  fun entityManagerProvider(entityManager: EntityManager): EntityManagerProvider {
    return EntityManagerProvider { entityManager }
  }

  @Bean
  fun transactionManager(entityManagerFactory: EntityManagerFactory): JpaTransactionManager =
      JpaTransactionManager(entityManagerFactory)

  @Bean
  fun snapshotTriggerDefinition(snapshotter: Snapshotter): SnapshotTriggerDefinition {
    return EventCountSnapshotTriggerDefinition(snapshotter, 2)
  }
}
