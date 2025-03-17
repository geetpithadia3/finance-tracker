package com.financetracker.infrastructure.adapters.inbound

import com.financetracker.application.ports.input.AllocationManagementUseCase
import com.financetracker.infrastructure.adapters.inbound.dto.response.AllocationResponse
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.UserRepository
import java.time.YearMonth
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/allocation")
class AllocationController(
    private val allocationManagementUseCase: AllocationManagementUseCase,
    val userRepository: UserRepository
) : BaseController(userRepository) {

  @GetMapping
  fun getAllocation(
      @RequestParam("yearMonth") @DateTimeFormat(pattern = "yyyy-MM") yearMonth: YearMonth,
  ): ResponseEntity<AllocationResponse> {
    val user = getCurrentUser()
    val allocation = allocationManagementUseCase.getAllocation(yearMonth, user)
    return ResponseEntity.ok(allocation)
  }
}
