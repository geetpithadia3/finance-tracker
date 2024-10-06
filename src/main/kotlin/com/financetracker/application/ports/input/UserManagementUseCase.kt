package com.financetracker.application.ports.input

import com.financetracker.infrastructure.adapters.inbound.dto.request.LoginRequest
import com.financetracker.infrastructure.adapters.inbound.dto.request.RegisterRequest
import com.financetracker.infrastructure.adapters.inbound.dto.response.AuthResponse
import java.util.*

interface UserManagementUseCase {

  fun register(request: RegisterRequest): UUID

  fun login(request: LoginRequest): AuthResponse

  fun addExternalCredentials(userId: UUID, externalId: String, externalKey: String)
}
