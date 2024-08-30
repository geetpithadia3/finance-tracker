package com.financetracker.domain.model

import java.util.*

data class User(
    val id: UUID? = null,
    val username: String,
    val password: String,
    val externalId: String? = null,
    val externalKey: String? = null
)
