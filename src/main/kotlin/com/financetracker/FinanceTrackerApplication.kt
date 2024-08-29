package com.financetracker

import org.axonframework.eventhandling.tokenstore.jpa.TokenEntry
import org.axonframework.eventsourcing.eventstore.jpa.DomainEventEntry
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.integration.annotation.IntegrationComponentScan
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

@SpringBootApplication
@EntityScan(
    basePackageClasses = [DomainEventEntry::class, TokenEntry::class],
    basePackages = ["com.financetracker.infrastructure.adapters.outbound.persistence.entity"])
@IntegrationComponentScan("com.financetracker")
@EnableWebSecurity
class FinanceTrackerApplication

fun main(args: Array<String>) {
  runApplication<FinanceTrackerApplication>(*args)
}
