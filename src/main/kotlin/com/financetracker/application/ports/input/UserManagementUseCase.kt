package com.financetracker.application.ports.input

import com.financetracker.application.dto.request.LoginRequest
import com.financetracker.application.dto.request.RegisterRequest

interface UserManagementUseCase {

  fun register(request: RegisterRequest): Long

  fun login(request: LoginRequest): String
}
