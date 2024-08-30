package com.financetracker.application.ports.input

import com.financetracker.infrastructure.adapters.inbound.dto.response.CategoryResponse

interface CategoryManagementUseCase {

  fun list(): List<CategoryResponse>
}
