package com.financetracker.infrastructure.adapters.inbound.dto.request

data class CreateCategoryRequest(val name: String, val isEditable: Boolean = true)

data class UpdateCategoryRequest(val name: String, val isActive: Boolean, val isEditable: Boolean)
