package com.financetracker.infrastructure.adapters.inbound.dto.response

import java.util.UUID

data class CategoryResponse(
    val id: UUID,
    val name: String,
    val isActive: Boolean,
    val isEditable: Boolean
)
