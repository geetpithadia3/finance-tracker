package com.financetracker.application.ports.input

interface CategoryManagementUseCase {

  fun list(): List<String>
}
