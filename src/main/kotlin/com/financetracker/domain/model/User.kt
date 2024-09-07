package com.financetracker.domain.model

import java.util.*

data class User(
    val id: UUID? = null,
    val username: String,
    val password: String,
    var externalId: String? = null,
    var externalKey: String? = null
)
