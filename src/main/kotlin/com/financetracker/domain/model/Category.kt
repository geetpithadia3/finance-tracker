package com.financetracker.domain.model

import java.util.*

data class Category(
    val id: UUID? = null,
    val name: String,
    val isActive: Boolean = true,
    val isEditable: Boolean = true,
    val userId: UUID
)
