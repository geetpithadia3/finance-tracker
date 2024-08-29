package com.financetracker.application.ports.input

import com.financetracker.application.dto.response.CategoryResponse

interface CategoryManagementUseCase {

  fun list(): List<CategoryResponse>
}
