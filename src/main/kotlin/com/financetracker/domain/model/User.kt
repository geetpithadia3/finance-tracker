package com.financetracker.domain.model

import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import java.util.*

data class User(
    val id: UUID? = null,
    val username: String,
    val password: String,
    var externalId: String? = null,
    var externalKey: String? = null
    )

fun User.toEntity(): UserEntity {
  val userEntity =
      UserEntity().apply {
        username = this@toEntity.username
        password = this@toEntity.password
      }

  if (this@toEntity.id != null) {
    userEntity.apply { id = this@toEntity.id!! }
  }

  return userEntity
}
