package com.financetracker.application

import com.financetracker.application.ports.input.UserManagementUseCase
import com.financetracker.application.ports.output.UserPersistence
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.LoginRequest
import com.financetracker.infrastructure.adapters.inbound.dto.request.RegisterRequest
import com.financetracker.infrastructure.security.JwtTokenUtil
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userPersistence: UserPersistence,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenUtil: JwtTokenUtil
) : UserManagementUseCase {
  override fun register(request: RegisterRequest): UUID {
    val user =
        User(username = request.username, password = passwordEncoder.encode(request.password))
    return userPersistence.save(user)
  }

  override fun login(request: LoginRequest): String {
    authenticationManager.authenticate(
        UsernamePasswordAuthenticationToken(request.username, request.password))
    return jwtTokenUtil.generateToken(request.username)
  }
}
