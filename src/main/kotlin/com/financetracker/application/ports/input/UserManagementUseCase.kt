package com.financetracker.application.ports.input

import com.financetracker.infrastructure.adapters.inbound.dto.request.LoginRequest
import com.financetracker.infrastructure.adapters.inbound.dto.request.RegisterRequest

interface UserManagementUseCase {

  fun register(request: RegisterRequest): Long

  fun login(request: LoginRequest): String
}
