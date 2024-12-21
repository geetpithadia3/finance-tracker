package com.financetracker.infrastructure.adapters.inbound.dto.request

import java.util.*

data class CreateCategoryRequest(val name: String, val isEditable: Boolean = true)

data class UpdateCategoryRequest(
    val id: UUID,
    val name: String,
    val isActive: Boolean,
    val isEditable: Boolean
)
