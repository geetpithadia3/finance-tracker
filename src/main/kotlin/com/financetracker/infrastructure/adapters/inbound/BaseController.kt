package com.financetracker.infrastructure.adapters.inbound

import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder

abstract class BaseController(private val userRepository: UserRepository) {
  protected fun getCurrentUser(): User {
    val authentication = SecurityContextHolder.getContext().authentication
    val username = authentication.name
    val entity = userRepository.findByUsername(username) ?: throw RuntimeException("User not found")

    return User(id = entity.id, username = entity.username, password = entity.password)
  }
}
