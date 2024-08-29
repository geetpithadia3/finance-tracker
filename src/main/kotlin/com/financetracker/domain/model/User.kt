package com.financetracker.domain.model

data class User(
    val id: Long? = null,
    val username: String,
    val password: String,
    val externalId: String? = null,
    val externalKey: String? = null
)
