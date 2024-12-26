// package com.financetracker.infrastructure.adapters.inbound
//
// import com.financetracker.application.ports.input.PayScheduleManagementUseCase
// import com.financetracker.domain.model.User
// import com.financetracker.infrastructure.adapters.inbound.dto.request.AddPayScheduleRequest
// import com.financetracker.infrastructure.adapters.outbound.persistence.repository.UserRepository
// import org.springframework.http.ResponseEntity
// import org.springframework.security.core.context.SecurityContextHolder
// import org.springframework.web.bind.annotation.PostMapping
// import org.springframework.web.bind.annotation.RequestBody
// import org.springframework.web.bind.annotation.RequestMapping
// import org.springframework.web.bind.annotation.RestController
// import java.util.*
//
// @RestController
// @RequestMapping("/paySchedule")
// class PaySchedulerController(
//    private val payScheduleManagementUseCase: PayScheduleManagementUseCase,
//    private val userRepository: UserRepository
// ) {
//
//  @PostMapping
//  fun create(@RequestBody request: AddPayScheduleRequest): ResponseEntity<UUID> {
//    val user = getCurrentUser()
//    return ResponseEntity.ok(payScheduleManagementUseCase.addPaySchedule(request, user))
//  }
//
//  private fun getCurrentUser(): User {
//    val authentication = SecurityContextHolder.getContext().authentication
//    val username = authentication.name
//    val userEntity =
//        userRepository.findByUsername(username) ?: throw RuntimeException("User not found")
//    return User(
//        id = userEntity.id,
//        username = userEntity.username,
//        password = userEntity.password,
//        externalId = userEntity.externalId,
//        externalKey = userEntity.externalKey)
//  }
// }
