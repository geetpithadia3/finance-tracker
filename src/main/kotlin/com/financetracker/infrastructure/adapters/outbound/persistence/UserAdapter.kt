package com.financetracker.infrastructure.adapters.outbound.persistence

import com.financetracker.application.ports.output.UserPersistence
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.UserRepository
import com.financetracker.infrastructure.adapters.outbound.splitwise.SplitwiseService
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserAdapter(val userRepository: UserRepository, val splitwiseService: SplitwiseService) :
    UserPersistence {
  override fun save(user: User): UUID {
    return userRepository
        .save(
            UserEntity().apply {
              username = user.username
              password = user.password
            })
        .id
  }

  override fun update(user: User): UUID {
    val existingUser =
        userRepository.findById(user.id!!).orElseThrow {
          NoSuchElementException("User not found with id: ${user.id}")
        }

    // Update only the fields that are provided in the input transaction
    existingUser.apply {
      externalKey = user.externalKey
      externalId = splitwiseService.getCurrentUserId(user.externalKey!!)
    }

    return userRepository.save(existingUser).id
  }

  override fun findById(id: UUID): User? {
    return userRepository
        .findById(id)
        .map { entity ->
          User(
              id = entity.id,
              username = entity.username,
              password = entity.password,
              externalId = entity.externalId,
              externalKey = entity.externalKey)
        }
        .orElse(null)
  }
}
