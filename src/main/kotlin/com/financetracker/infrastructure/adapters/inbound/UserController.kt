package com.financetracker.infrastructure.adapters.inbound

import com.financetracker.application.ports.input.UserManagementUseCase
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.AddExternalCredentialsRequest
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val userManagementUseCase: UserManagementUseCase,
    private val userRepository: UserRepository
) {

  private val logger = LoggerFactory.getLogger(TransactionController::class.java)

  @PutMapping("/user/external-credentials")
  fun addExternalCredentials(
      @RequestBody request: AddExternalCredentialsRequest
  ): ResponseEntity<Unit> {
    logger.info(
        "Received request to add external credentials for user: ${getCurrentUser().username}")
    return try {
      val user = getCurrentUser()
      userManagementUseCase.addExternalCredentials(user.id!!, request.externalKey)
      logger.info("External credentials added successfully for user: ${user.username}")
      ResponseEntity.ok().build()
    } catch (e: Exception) {
      logger.error("Error adding external credentials for user: ${getCurrentUser().username}", e)
      throw e
    }
  }

  //  @GetMapping("/user/friends")
  //  fun getFriends(): ResponseEntity<List<FriendsResponse>> {
  //    logger.info("Received request to fetch friends for user: ${getCurrentUser().username}")
  //    return try {
  //      val user = getCurrentUser()
  //      val friendsResponse = userManagementUseCase.getFriends(user)
  //      logger.info("Fetched ${friendsResponse.size} friends for user: ${user.username}")
  //      ResponseEntity.ok(friendsResponse)
  //    } catch (e: Exception) {
  //      logger.error("Error getting friends for user: ${getCurrentUser().username}", e)
  //      throw e
  //    }
  //  }

  private fun getCurrentUser(): User {
    val authentication = SecurityContextHolder.getContext().authentication
    val username = authentication.name
    val entity = userRepository.findByUsername(username) ?: throw RuntimeException("User not found")
    return User(
        id = entity.id,
        username = entity.username,
        password = entity.password,
        externalId = entity.externalId,
        externalKey = entity.externalKey)
  }
}
