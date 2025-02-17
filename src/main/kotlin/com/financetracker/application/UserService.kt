package com.financetracker.application

import com.financetracker.application.ports.input.UserManagementUseCase
import com.financetracker.application.ports.output.UserPersistence
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.LoginRequest
import com.financetracker.infrastructure.adapters.inbound.dto.request.RegisterRequest
import com.financetracker.infrastructure.adapters.inbound.dto.response.AuthResponse
import com.financetracker.infrastructure.security.JwtTokenUtil
import java.util.*
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userPersistence: UserPersistence,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenUtil: JwtTokenUtil,
    private val categorySeedingService: CategorySeedingService
) : UserManagementUseCase {
  override fun register(request: RegisterRequest): UUID {
    val user =
        User(username = request.username, password = passwordEncoder.encode(request.password))
    val userId = userPersistence.save(user)

    categorySeedingService.seedDefaultCategories(userId)

    return userId
  }

  override fun login(request: LoginRequest): AuthResponse {
    authenticationManager.authenticate(
        UsernamePasswordAuthenticationToken(request.username, request.password))
    return AuthResponse(jwtTokenUtil.generateToken(request.username))
  }
}
