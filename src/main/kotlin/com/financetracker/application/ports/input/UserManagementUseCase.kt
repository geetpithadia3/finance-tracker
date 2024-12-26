package com.financetracker.application.ports.input

import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.LoginRequest
import com.financetracker.infrastructure.adapters.inbound.dto.request.RegisterRequest
import com.financetracker.infrastructure.adapters.inbound.dto.response.AuthResponse
import com.financetracker.infrastructure.adapters.inbound.dto.response.FriendsResponse
import java.util.*

interface UserManagementUseCase {

  fun register(request: RegisterRequest): UUID

  fun login(request: LoginRequest): AuthResponse

  fun addExternalCredentials(userId: UUID, externalKey: String)

  fun getFriends(user: User): List<FriendsResponse>
}
