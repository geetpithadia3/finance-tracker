package com.financetracker.infrastructure.adapters.inbound

import com.financetracker.application.ports.input.UserManagementUseCase
import com.financetracker.infrastructure.adapters.inbound.dto.request.LoginRequest
import com.financetracker.infrastructure.adapters.inbound.dto.request.RegisterRequest
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/auth")
class AuthController(private val userManagementUseCase: UserManagementUseCase) {

  private val logger = LoggerFactory.getLogger(AuthController::class.java)

  @PostMapping("/register")
  fun register(@RequestBody request: RegisterRequest): ResponseEntity<UUID> {
    logger.info("Received registration request for user: ${request.username}")
    return try {
      val userId = userManagementUseCase.register(request)
      logger.info("User registered successfully: ${request.username}, userId: $userId")
      ResponseEntity.ok(userId)
    } catch (e: Exception) {
      logger.error("Error during user registration: ${request.username}", e)
      throw e
    }
  }

  @PostMapping("/login")
  fun login(@RequestBody request: LoginRequest): ResponseEntity<String> {
    logger.info("Received login request for user: ${request.username}")
    return try {
      val token = userManagementUseCase.login(request)
      logger.info("User logged in successfully: ${request.username}")
      ResponseEntity.ok(token)
    } catch (e: Exception) {
      logger.error("Error during user login: ${request.username}", e)
      throw e
    }
  }
}
