package com.financetracker.infrastructure.adapters.outbound.persistence

import com.financetracker.application.ports.output.UserPersistence
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserAdapter(val userRepository: UserRepository) : UserPersistence {
  override fun save(user: User): UUID {
    return userRepository
        .save(
            UserEntity().apply {
              username = user.username
              password = user.password
            })
        .id
  }
}
