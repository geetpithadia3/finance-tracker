package com.financetracker.infrastructure.security

import com.financetracker.infrastructure.adapters.outbound.persistence.respository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {

  override fun loadUserByUsername(username: String): UserDetails {
    val user =
        userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found with username: $username")

    return user as UserDetails // Assuming your User entity implements UserDetails
  }
}
