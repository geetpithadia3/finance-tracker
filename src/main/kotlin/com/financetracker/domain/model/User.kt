package com.financetracker.domain.model

data class User(
    val id: Long? = 0,
    val username: String,
    val password: String,
    val externalId: String? = null,
    val externalKey: String? = null
)
