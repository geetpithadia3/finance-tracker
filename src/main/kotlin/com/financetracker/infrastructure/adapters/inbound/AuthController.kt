package com.financetracker.infrastructure.adapters.inbound

import com.financetracker.application.AuthService
import com.financetracker.application.dto.request.LoginRequest
import com.financetracker.application.dto.request.RegisterRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(private val authService: AuthService) {

  @PostMapping("/register")
  fun register(@RequestBody request: RegisterRequest): ResponseEntity<String> {
    return ResponseEntity.ok(authService.register(request))
  }

  @PostMapping("/login")
  fun login(@RequestBody request: LoginRequest): ResponseEntity<String> {
    return ResponseEntity.ok(authService.login(request))
  }
}
