package com.financetracker.infrastructure.adapters.outbound.persistence.repository

import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<UserEntity, UUID> {
  fun findByUsername(username: String): UserEntity?

  @Query(value = "SELECT encrypt_api_key(:apiKey, :secretKey)", nativeQuery = true)
  fun encryptApiKey(
      @Param("apiKey") apiKey: String?,
      @Param("secretKey") secretKey: String?
  ): String?
}
