package com.financetracker.infrastructure.adapters.outbound.persistence

import com.financetracker.application.ports.output.UserPersistence
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.respository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserAdapter(val userRepository: UserRepository) : UserPersistence {
  override fun save(user: User): Long {
    return userRepository
        .save(
            UserEntity().apply {
              username = user.username
              password = user.password
            })
        .id!!
  }
}
