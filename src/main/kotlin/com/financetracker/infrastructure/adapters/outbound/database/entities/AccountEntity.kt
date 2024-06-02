package com.financetracker.infrastructure.adapters.outbound.database.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.*

@Entity(name = "account")
class AccountEntity(
    @Id val id: UUID,
    @Column(name = "name") val name: String,
    @Column(name = "type") val type: String,
    @Column(name = "description") val description: String,
    @Column(name = "organization") val organization: String
)
