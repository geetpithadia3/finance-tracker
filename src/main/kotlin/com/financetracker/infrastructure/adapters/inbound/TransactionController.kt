package com.financetracker.infrastructure.adapters.inbound

import com.financetracker.application.ports.input.TransactionManagementUseCase
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.AddTransactionRequest
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.slf4j.LoggerFactory

@RestController
class TransactionController(
    val transactionManagementUseCase: TransactionManagementUseCase,
    val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(TransactionController::class.java)

    @PostMapping("/transactions")
    fun addTransactions(@RequestBody request: List<AddTransactionRequest>): ResponseEntity<Unit> {
        val user = getCurrentUser()
        logger.info("Received request to add ${request.size} transactions for user: ${user.username}")
        return try {
            transactionManagementUseCase.add(request, user)
            logger.info("Successfully added ${request.size} transactions for user: ${user.username}")
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            logger.error("Error adding transactions for user: ${user.username}", e)
            throw e
        }
    }

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
