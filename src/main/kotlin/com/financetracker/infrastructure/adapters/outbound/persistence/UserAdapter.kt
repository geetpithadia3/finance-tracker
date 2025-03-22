package com.financetracker.infrastructure.adapters.outbound.persistence

import com.financetracker.application.ports.output.UserPersistence
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.UserRepository
import com.financetracker.infrastructure.adapters.outbound.persistence.utils.toEntity
import java.util.*
import org.springframework.stereotype.Service

@Service
class UserAdapter(val userRepository: UserRepository) : UserPersistence {
  override fun save(user: User): UUID {
    return userRepository.save(user.toEntity()).id
  }

  override fun findById(id: UUID): User? {
    return userRepository
        .findById(id)
        .map { entity ->
          User(
              id = entity.id,
              username = entity.username,
              password = entity.password,
          )
        }
        .orElse(null)
  }
}
